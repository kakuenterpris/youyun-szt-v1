package com.ustack.chat.service.impl;

import cn.xfyun.model.sign.LfasrSignature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.ustack.chat.entity.BusUserMeetingAudioEntity;
import com.ustack.chat.entity.BusUserMeetingContentEntity;
import com.ustack.chat.mapper.BusUserMeetingAudioMapper;
import com.ustack.chat.mappings.BusUserMeetingAudioMapping;
import com.ustack.chat.mappings.BusUserMeetingContentMapping;
import com.ustack.chat.repo.BusUserMeetingAudioRepo;
import com.ustack.chat.repo.BusUserMeetingContentRepo;
import com.ustack.chat.scheduled.MeetingAudioLongFromRsaScheduled;
import com.ustack.chat.service.BusUserMeetingAudioService;
import com.ustack.chat.service.BusUserMeetingContentService;
import com.ustack.chat.util.DateUtil;
import com.ustack.chat.util.HttpUtils;
import com.ustack.feign.client.FileApi;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.meeting.constants.AudioConstants;
import com.ustack.meeting.dto.ProgressParamDTO;
import com.ustack.meeting.dto.UserMeetingAudioContentDTO;
import com.ustack.meeting.dto.XFResultDTO;
import com.ustack.meeting.enums.MeetingAudioErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Admin_14104
 * @description 针对表【bus_user_meeting_audio(用户会议纪要-语音文件表)】的数据库操作Service实现
 * @createDate 2025-03-25 11:28:49
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BusUserMeetingAudioServiceImpl extends ServiceImpl<BusUserMeetingAudioMapper, BusUserMeetingAudioEntity>
        implements BusUserMeetingAudioService {

    private static final String HOST = "https://raasr.xfyun.cn";
    private static final Gson gson = new Gson();
    @Value("${xfyun.appId}")
    private String xfAppId;

    @Value("${xfyun.lfasrSecretKey}")
    private String xfLfasrSecretKey;

    private final FileApi fileApi;
    private final RedisUtil redisUtil;
    private final BusUserMeetingAudioRepo audioRepo;
    private final BusUserMeetingAudioMapping audioMapping;
    private final BusUserMeetingContentRepo contentRepo;
    private final BusUserMeetingContentService contentService;
    private final BusUserMeetingContentMapping contentMapping;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse audioToChar(UserMeetingAudioContentDTO audioContentDTO) {
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        String fileId = audioContentDTO.getFileId();
        String fileType = audioContentDTO.getFileType();
        String fileSize = audioContentDTO.getSize();
        if (StringUtils.isAllBlank(fileId, fileType, fileSize)) {
            return RestResponse.fail(MeetingAudioErrorCode.ADD_FAIL.getCode(), "参数为空");
        }
        // 新增语音文件表
        BusUserMeetingAudioEntity audioEntity = audioMapping.dto2Entity(audioContentDTO);
        audioEntity.setUserId(userId);
        audioEntity.setIsDeleted(0);
        // 判断之前是否上传过该文件，若上传过，则删除旧纪录
        BusUserMeetingAudioEntity audioRepoOne = audioRepo.getOne(new QueryWrapper<>(audioEntity));
        if (audioRepoOne != null) {
            audioRepo.logicDelete(audioRepoOne.getId());
            LambdaQueryWrapper<BusUserMeetingContentEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BusUserMeetingContentEntity::getFileId, fileId)
                    .eq(BusUserMeetingContentEntity::getUserId, userId)
                    .eq(BusUserMeetingContentEntity::getAudioId, audioRepoOne.getId())
                    .eq(BusUserMeetingContentEntity::getIsDeleted, false);
            BusUserMeetingContentEntity contentRepoOne = contentRepo.getOne(wrapper);
            if (contentRepoOne != null) {
                contentRepo.logicDelete(contentRepoOne.getId());
            }
        }
        Integer sort = audioRepo.getMaxSort() + 1;
        audioEntity.setSort(sort);
        audioEntity.setUserId(userId);
        boolean af = audioRepo.save(audioEntity);
        Long audioId = audioEntity.getId();
        // 新增语音内容表-未转换完成时，内容为空
        BusUserMeetingContentEntity contentEntity = new BusUserMeetingContentEntity(userId, fileId, "", audioId);
        Long contentId = contentRepo.add(contentEntity);
        // 异步语音文件转文字
        runLfasr(audioEntity.getId(), fileId, userId, contentId);
        return (af && contentId != null) ? RestResponse.SUCCESS : RestResponse.fail(MeetingAudioErrorCode.ADD_FAIL.getCode(), MeetingAudioErrorCode.ADD_FAIL.getMsg());
    }

    @Override
    public RestResponse transRetry(UserMeetingAudioContentDTO audioContentDTO) {
        if (null == audioContentDTO || StringUtils.isBlank(audioContentDTO.getFileId())
                || null == audioContentDTO.getId() || 0 == audioContentDTO.getId()
                || null == audioContentDTO.getContentId() || 0 == audioContentDTO.getContentId()) {
            return RestResponse.fail(MeetingAudioErrorCode.ADD_FAIL.getCode(), "参数为空");
        }
        String fileId = audioContentDTO.getFileId();
        Long audioId = audioContentDTO.getId();
        Long contentId = audioContentDTO.getContentId();
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        // 转换之前,设置是否转换为初始状态，进度为0
        audioRepo.updateByIsTrans(audioId);
        String proKey = AudioConstants.PROGRESS_REDIS_KEY + userId + ":" + fileId;
        Object obj = redisUtil.get(proKey);
        long expireTime;
        if (obj != null) {
            expireTime = redisUtil.getExpire(proKey);
        } else {
            expireTime = AudioConstants.EXPIRE_DATE_30_MIN;
        }
        redisUtil.set(proKey, 8, expireTime);
        // 重新转换
        runLfasr(audioId, fileId, userId, contentId);
        return RestResponse.SUCCESS;
    }

    @Override
    public RestResponse queryAudioFileList(String queryParam, Integer start, Integer size) {
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        start = (start - 1) * size; // 计算从第几条数据开始
        List<UserMeetingAudioContentDTO> audioContentDTOS = audioRepo.getAudioList(userId, queryParam, start, size);
        // 转换时间
        for (UserMeetingAudioContentDTO audioContentDTO : audioContentDTOS) {
            audioContentDTO.setRealDurationString(DateUtil.millisecondConversionTime(audioContentDTO.getRealDuration()));
        }
        return RestResponse.success(audioContentDTOS, audioContentDTOS.size());
    }

    @Override
    public RestResponse queryLongAsrProgress(List<ProgressParamDTO> progressParamDTOS) {

        if (null == progressParamDTOS || progressParamDTOS.isEmpty()) {
            return RestResponse.fail(MeetingAudioErrorCode.FILE_ID_NULL.getCode(), MeetingAudioErrorCode.FILE_ID_NULL.getMsg());
        }
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();

        List<Map<String, Object>> resultList = new ArrayList<>(progressParamDTOS.size());

        for (ProgressParamDTO paramDTO : progressParamDTOS) {
            String fileId = paramDTO.getFileId();
            String audioRedisKey = AudioConstants.PROGRESS_REDIS_KEY + userId + ":" + fileId;
            Object o = redisUtil.get(audioRedisKey);
            Map<String, Object> map = new HashMap<>(2);

            if (null == o) {
                map.put(AudioConstants.FILE_ID, fileId);
                map.put(AudioConstants.PROGRESS_NUM, 0);
                map.put(AudioConstants.REAL_DURATION_STRING, "");
            } else {
                Integer pro = Integer.parseInt(String.valueOf(o));
                map.put(AudioConstants.FILE_ID, fileId);
                map.put(AudioConstants.PROGRESS_NUM, pro);

                Long contentId = paramDTO.getContentId();
                if (pro == 100) { // 进度100，查时间
                    BusUserMeetingContentEntity byId = contentRepo.getById(contentId);
                    if (null == byId) {
                        map.put(AudioConstants.REAL_DURATION_STRING, "");
                    } else {
                        map.put(AudioConstants.REAL_DURATION_STRING, DateUtil.millisecondConversionTime(byId.getRealDuration()));
                    }
                } else {
                    map.put(AudioConstants.REAL_DURATION_STRING, "");
                }
            }
            resultList.add(map);
        }
        return RestResponse.success(resultList);
    }

    @Override
    public RestResponse queryAudioFileAndContent(String contentId) {
        if (StringUtils.isBlank(contentId)) {
            return RestResponse.fail(MeetingAudioErrorCode.ID_NULL.getCode(), MeetingAudioErrorCode.ID_NULL.getMsg());
        }
        BusUserMeetingContentEntity contentEntity = contentRepo.getById(contentId);
        UserMeetingAudioContentDTO audioContentDTO = null;
        if (contentEntity != null) {
            audioContentDTO = contentMapping.entity2Dto(contentEntity);
            audioContentDTO.setRealDurationString(DateUtil.millisecondConversionTime(contentEntity.getRealDuration()));
            BusUserMeetingAudioEntity byId1 = audioRepo.getById(contentEntity.getAudioId());
            audioContentDTO.setFileOriginName(byId1.getFileOriginName());
            audioContentDTO.setFileType(byId1.getFileType());
            audioContentDTO.setSize(byId1.getSize());
            audioContentDTO.setContentId(contentEntity.getId());
        }
        return RestResponse.success(audioContentDTO);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse updateAudioEntity(UserMeetingAudioContentDTO audioContentDTO) {

        if (null == audioContentDTO || StringUtils.isBlank(audioContentDTO.getFileOriginName()) || null == audioContentDTO.getId()
                || 0 == audioContentDTO.getId()) {
            return RestResponse.fail(MeetingAudioErrorCode.EDIT_FAIL.getCode(), "参数为空");
        }
        BusUserMeetingAudioEntity audio = audioRepo.getById(audioContentDTO.getId());
        if (audio != null) {
            audio.setFileOriginName(audioContentDTO.getFileOriginName());
            audioRepo.updateById(audio);
        }
        return RestResponse.SUCCESS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse updateContent(UserMeetingAudioContentDTO audioContentDTO) {

        if (null == audioContentDTO || null == audioContentDTO.getContentId() || StringUtils.isBlank(audioContentDTO.getContent())) {
            return RestResponse.fail(MeetingAudioErrorCode.EDIT_FAIL.getCode(), "参数为空");
        }
        BusUserMeetingContentEntity byId = contentRepo.getById(audioContentDTO.getContentId());
        if (byId != null) {
            byId.setContent(audioContentDTO.getContent());
            contentRepo.updateById(byId);
        }
        return RestResponse.SUCCESS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse logicDelete(String id) {
        if (StringUtils.isBlank(id)) {
            return RestResponse.fail(MeetingAudioErrorCode.DELETE_FAIL.getCode(), "ID为空");
        }
        UserMeetingAudioContentDTO audioRepoEntity = audioRepo.getEntity(Long.parseLong(id));
        if (audioRepoEntity != null) {
            // 语音文件表逻辑删除
            audioRepo.logicDelete(Long.parseLong(id));
            // 语音内容表逻辑删除
            contentRepo.logicDelete(audioRepoEntity.getContentId());
        }
        return RestResponse.SUCCESS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse permanentlyDelete(String id) {
        if (StringUtils.isBlank(id)) {
            return RestResponse.fail(MeetingAudioErrorCode.DELETE_FAIL.getCode(), "参数为空");
        }
        UserMeetingAudioContentDTO audioRepoEntity = audioRepo.getDeletedEntity(Long.parseLong(id));
        if (audioRepoEntity != null) {
            // 语音文件表物理删除
            audioRepo.deleteById(Long.parseLong(id));
            // 语音内容表物理删除
            contentRepo.deleteById(audioRepoEntity.getContentId());
            // 删除文件记录表和服务器文件
            fileApi.deleteFileCommon(audioRepoEntity.getFileId());
        }
        return RestResponse.SUCCESS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse clearRecycle() {
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        int row = audioRepo.clearRecycle(userId);
        log.info("清空当前用户：{},回收站：{} 条数据", ContextUtil.getUserName(), row);
        return RestResponse.SUCCESS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse fileRestore(UserMeetingAudioContentDTO audioContentDTO) {
        if (null == audioContentDTO || null == audioContentDTO.getId() || 0 == audioContentDTO.getId()) {
            return RestResponse.fail(MeetingAudioErrorCode.EDIT_FAIL.getCode(), "参数为空");
        }
        UserMeetingAudioContentDTO audioRepoEntity = audioRepo.getDeletedEntity(audioContentDTO.getId());
        if (audioRepoEntity != null) {
            // 语音文件表还原
            audioRepo.restore(audioRepoEntity.getId());
            // 语音内容表还原
            contentRepo.restore(audioRepoEntity.getAudioId());
        }
        return RestResponse.SUCCESS;
    }

    @Override
    public RestResponse queryAudioFileRecycleList(String queryParam, Integer start, Integer size) {
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        start = (start - 1) * size; // 计算从第几条数据开始
        List<UserMeetingAudioContentDTO> audioRecycleList = audioRepo.getAudioRecycleList(userId, queryParam, start, size);
        for (UserMeetingAudioContentDTO audioContentDTO : audioRecycleList) {
            audioContentDTO.setRealDurationString(DateUtil.millisecondConversionTime(audioContentDTO.getRealDuration()));
        }
        return RestResponse.success(audioRecycleList, audioRecycleList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse xfWebApiCallback(String orderId, int status) {
        try {
            log.info("讯飞语音接口回调开始");
            HashMap<String, Object> map = new HashMap<>(16);
            map.put("orderId", orderId);
            LfasrSignature lfasrSignature = new LfasrSignature(xfAppId, xfLfasrSecretKey);
            map.put("signa", lfasrSignature.getSigna());
            map.put("ts", lfasrSignature.getTs());
            map.put("appId", xfAppId);
            map.put("resultType", "transfer,predict");
            String paramString = HttpUtils.parseMapToPathParam(map);
            log.info("语音回调接口，获取转写结果请求参数：" + paramString);
            String url = HOST + "/v2/api/getResult" + "?" + paramString;
            log.info("语音回调接口，获取转写结果请求地址：" + url);
            String response = HttpUtils.iflyrecGet(url);
            log.info("语音回调接口，获取转写结果响应：" + response);
            XFResultDTO xfResultDTO = gson.fromJson(response, XFResultDTO.class);

            // 订单流程状态
            // 0：订单已创建
            // 3：订单处理中
            // 4：订单已完成
            // -1：订单失败
            if (xfResultDTO.getContent() != null && xfResultDTO.getContent().getOrderInfo() != null) {
                BusUserMeetingAudioEntity audioEntity = audioRepo.getByOrderId(orderId);
                BusUserMeetingContentEntity contentEntity = contentRepo.getByOrderId(orderId);
                if (audioEntity != null) {
                    // 修改Redis中进度值
                    String redisAudioKey = AudioConstants.PROGRESS_REDIS_KEY + audioEntity.getUserId() + ":" + audioEntity.getFileId();
                    Object obj = redisUtil.get(redisAudioKey);
                    long expireTime;
                    if (obj != null) {
                        expireTime = redisUtil.getExpire(redisAudioKey);
                    } else {
                        expireTime = AudioConstants.EXPIRE_DATE_30_MIN;
                    }
                    // 订单已完成
                    if (xfResultDTO.getContent().getOrderInfo().getStatus() == 4) {
                        audioEntity.setOrderId(orderId);
                        // 更新语音文件表状态
                        audioEntity.setIsTrans(AudioConstants.TRANS_SUCCESS);
                        audioRepo.updateById(audioEntity);
                        redisUtil.set(redisAudioKey, 100, expireTime);
                        log.info("语音转写成功");
                    }
                    // 订单失败
                    if (xfResultDTO.getContent().getOrderInfo().getStatus() == -1) {
                        audioEntity.setOrderId(orderId);
                        // 更新语音文件表状态
                        audioEntity.setIsTrans(AudioConstants.TRANS_FAIL);
                        audioRepo.updateById(audioEntity);
                        redisUtil.set(redisAudioKey, -1, expireTime);
                        log.error("语音转写失败");
                    }
                } else {
                    log.error("语音文件表中未查询到该orderId：" + orderId);
                }
                if (contentEntity != null) {
                    // 将执行内容更新到语音内容表中
                    contentEntity.setContent(xfResultDTO.getContent().getOrderResult());
                    contentEntity.setOrderId(orderId);
                    contentEntity.setRealDuration(xfResultDTO.getContent().getOrderInfo().getRealDuration());
                    contentService.updateById(contentEntity);
                } else {
                    log.error("语音内容表中未查询到该orderId：" + orderId);
                }
            } else {
                log.error("讯飞语音接口调用失败：" + xfResultDTO.getDescInfo());
            }
            log.info("讯飞语音接口回调结束");
            return RestResponse.success(xfResultDTO);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private void runLfasr(Long audioId, String fileId, String userId, Long contentId) {
        MeetingAudioLongFromRsaScheduled scheduled = new MeetingAudioLongFromRsaScheduled(fileId, xfAppId, xfLfasrSecretKey,
                redisUtil, userId, contentService, contentId, audioRepo, audioId, fileApi);
        Thread thread = new Thread(scheduled);
        thread.start();
    }


}

































