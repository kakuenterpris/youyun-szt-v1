package com.thtf.chat.service.impl;

import cn.xfyun.api.LfasrClient;
import cn.xfyun.config.LfasrTaskStatusEnum;
import cn.xfyun.model.response.lfasr.LfasrMessage;
import cn.xfyun.model.sign.LfasrSignature;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thtf.chat.entity.BusUserMeetingAudioEntity;
import com.thtf.chat.entity.BusUserMeetingContentEntity;
import com.thtf.chat.mappings.BusUserMeetingAudioMapping;
import com.thtf.chat.mappings.BusUserMeetingContentMapping;
import com.thtf.chat.repo.BusUserMeetingAudioRepo;
import com.thtf.chat.repo.BusUserMeetingContentRepo;
import com.thtf.chat.scheduled.CommonTrans;
import com.thtf.chat.scheduled.MeetingAudioLongFromRsaScheduled;
import com.thtf.chat.service.BusUserMeetingAudioService;
import com.thtf.chat.mapper.BusUserMeetingAudioMapper;
import com.thtf.chat.service.BusUserMeetingContentService;
import com.thtf.chat.util.DateUtil;
import com.thtf.chat.util.HttpUtils;
import com.thtf.feign.client.FileApi;
import com.thtf.global.common.cache.RedisUtil;
import com.thtf.global.common.rest.ContextUtil;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.meeting.constants.AudioConstants;
import com.thtf.meeting.dto.ContentDTO;
import com.thtf.meeting.dto.ProgressParamDTO;
import com.thtf.meeting.dto.UserMeetingAudioContentDTO;
import com.thtf.meeting.dto.XFResultDTO;
import com.thtf.meeting.enums.MeetingAudioErrorCode;
import com.thtf.resource.enums.ResourceErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    public RestResponse queryAudioFileList(String queryParam) {
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        List<UserMeetingAudioContentDTO> audioContentDTOS = audioRepo.getAudioList(userId, queryParam);
        // 转换时间
        for (UserMeetingAudioContentDTO audioContentDTO : audioContentDTOS) {
            audioContentDTO.setRealDurationString(DateUtil.millisecondConversionTime(audioContentDTO.getRealDuration()));
        }
        return RestResponse.success(audioContentDTOS);
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
        UserMeetingAudioContentDTO audioRepoEntity = audioRepo.getEntity(Long.parseLong(id));
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
        UserMeetingAudioContentDTO audioRepoEntity = audioRepo.getEntity(audioContentDTO.getId());
        if (audioRepoEntity != null) {
            // 语音文件表还原
            audioRepo.restore(audioRepoEntity.getId());
            // 语音内容表还原
            contentRepo.restore(audioRepoEntity.getAudioId());
        }
        return RestResponse.SUCCESS;
    }

    @Override
    public RestResponse queryAudioFileRecycleList(String queryParam) {
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        List<UserMeetingAudioContentDTO> audioRecycleList = audioRepo.getAudioRecycleList(userId, queryParam);
        for (UserMeetingAudioContentDTO audioContentDTO : audioRecycleList) {
            audioContentDTO.setRealDurationString(DateUtil.millisecondConversionTime(audioContentDTO.getRealDuration()));
        }
        return RestResponse.success(audioRecycleList);
    }

    @Override
    public RestResponse xfWebApiCallback(String orderId, int status) {
        try {
            HashMap<String, Object> map = new HashMap<>(16);
            map.put("orderId", orderId);
            LfasrSignature lfasrSignature = new LfasrSignature(xfAppId, xfLfasrSecretKey);
            map.put("signa", lfasrSignature.getSigna());
            map.put("ts", lfasrSignature.getTs());
            map.put("appId", xfAppId);
            map.put("resultType", "transfer,predict");
            String paramString = HttpUtils.parseMapToPathParam(map);
            String url = HOST + "/v2/api/getResult" + "?" + paramString;
            log.info("获取转写结果请求地址：" + url);
            String response = HttpUtils.iflyrecGet(url);
            XFResultDTO xfResultDTO = gson.fromJson(response, XFResultDTO.class);

            // todo

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

































