package com.ustack.chat.scheduled;

import cn.hutool.json.JSONUtil;
import cn.xfyun.api.LfasrClient;
import cn.xfyun.config.LfasrTaskStatusEnum;
import cn.xfyun.model.response.lfasr.LfasrMessage;
import cn.xfyun.model.sign.LfasrSignature;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ustack.chat.entity.BusUserMeetingAudioEntity;
import com.ustack.chat.repo.BusUserMeetingAudioRepo;
import com.ustack.chat.util.HttpUtils;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.meeting.constants.AudioConstants;
import com.ustack.meeting.dto.XFResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Author：PingY
 * Classname：CommonTrans
 * Date：2025/3/28  11:33
 * Description:
 */
@Slf4j
public class CommonTrans {
    private static final String HOST = "https://raasr.xfyun.cn";
    private static final String callbackUrl = "http://127.0.0.1/chat-api/api/v1/meetingMinute/xfWebApiCallback";
    private static final Gson gson = new Gson();

    /**
     * 讯飞语音文件转文字（非实时）
     *
     * @param audioFilePath
     * @return
     */
    public static LfasrMessage xfyunLfasrSDK(String audioFilePath, String userId, String xfAppId, String xfLfasrSecretKey,
                                             String fileId, RedisUtil redisUtil, BusUserMeetingAudioRepo audioRepo, Long audioId) {
        long startTime = System.currentTimeMillis();
        boolean flag = true;
        String orderId = "";
        try {
            if (StringUtils.isBlank(audioFilePath)) {
                log.error("文件ID为：{} 的语音文件未找到,", fileId);  // 语音文件为空，则不执行转写任务
                throw new FileNotFoundException();
            }
//            audioFilePath = Objects.requireNonNull(MeetingAudioLongFromRsaScheduled.class.getResource("/")).toURI().getPath() + "/audio/lfasr.wav";
            //1、创建客户端实例
            LfasrClient lfasrClient = new LfasrClient.Builder(xfAppId, xfLfasrSecretKey).slice(102400).build();
            //2、上传
            LfasrMessage task = lfasrClient.upload(audioFilePath);
            String taskId = task.getData();
            log.info("转写任务 taskId：" + taskId);
            orderId = taskId;
            //3、查看转写进度
            int status = 0;

            if (StringUtils.isBlank(taskId)) {
                status = -1;
            }
            // 将进度写入Redis中
            Integer percentNum = 0;
            String redisAudioKey = AudioConstants.PROGRESS_REDIS_KEY + userId + ":" + fileId;
            // 语音转写开始将进度置为0
            Object obj = redisUtil.get(redisAudioKey);
            long expireTime;
            if (obj != null) {
                expireTime = redisUtil.getExpire(redisAudioKey);
            } else {
                expireTime = AudioConstants.EXPIRE_DATE_30_MIN;
            }
            redisUtil.set(redisAudioKey, percentNum, expireTime);

            while (LfasrTaskStatusEnum.STATUS_9.getKey() != status) {
                LfasrMessage message = lfasrClient.getProgress(taskId);
                log.info("转写任务 taskId：{}, fileId：{}", taskId, fileId);
                log.info("讯飞语音转文字进度：" + message.toString());
                Gson gson = new Gson();
                Map<String, String> map = gson.fromJson(message.getData(), new TypeToken<Map<String, String>>() {
                }.getType());
                if (map != null && map.get("status") != null) {
                    status = Integer.parseInt(map.get("status"));
                } else {
                    flag = false;
                    break;
                }
                if (status == LfasrTaskStatusEnum.STATUS_FAILED.getKey()) { // 失败。
                    percentNum = -1;
                } else if (status == LfasrTaskStatusEnum.STATUS_0.getKey()) {
                    percentNum = 0;
                } else if (status == LfasrTaskStatusEnum.STATUS_1.getKey()) {
                    percentNum = 10;
                } else if (status == LfasrTaskStatusEnum.STATUS_2.getKey()) {
                    percentNum = 30;
                } else if (status == LfasrTaskStatusEnum.STATUS_3.getKey()) {
                    percentNum = 50;
                } else if (status == LfasrTaskStatusEnum.STATUS_4.getKey()) {
                    percentNum = 70;
                } else if (status == LfasrTaskStatusEnum.STATUS_5.getKey()) {
                    percentNum = 90;
                } else if (status == LfasrTaskStatusEnum.STATUS_9.getKey()) {
                    percentNum = 100;
                }

                // 如果语音转写时间超过1个小时，则直接置为失败
                if (System.currentTimeMillis() - startTime > 3600 * 1000) {
                    flag = false;
                    percentNum = -1;
                }

                obj = redisUtil.get(redisAudioKey);
                if (obj != null) {
                    expireTime = redisUtil.getExpire(redisAudioKey);
                } else {
                    expireTime = AudioConstants.EXPIRE_DATE_30_MIN;
                }
                redisUtil.set(redisAudioKey, percentNum, expireTime);
                if (percentNum == -1) {
                    flag = false;
                    break;
                }
                TimeUnit.SECONDS.sleep(2);
            }
            if (flag) { // 转换成功，获取结果
                //4、获取结果
                LfasrMessage progress = lfasrClient.getResult(taskId);
                log.info("转写结果: \n" + progress.getData());
                if (progress.getOk() == 0 && !JSONArray.parseObject(progress.getData(), List.class).isEmpty()) {
                    // 更新语音文件表状态
                    BusUserMeetingAudioEntity byId = audioRepo.getById(audioId);
                    if (byId != null) {
                        byId.setOrderId(orderId);
                        byId.setIsTrans(AudioConstants.TRANS_SUCCESS);
                        audioRepo.updateById(byId);
                    }
                    return progress;
                } else {
                    log.error("转写失败");
                    flag = false;
                }
            }
        } catch (Exception e) {
            log.error("转写异常");
            flag = false;
            throw new RuntimeException(e);
        } finally {
            if (!flag) { // 转换失败，修改状态
                // 更新语音文件表状态 -失败
                BusUserMeetingAudioEntity byId = audioRepo.getById(audioId);
                if (byId != null) {
                    byId.setOrderId(orderId);
                    byId.setIsTrans(AudioConstants.TRANS_FAIL);
                    audioRepo.updateById(byId);
                }
                // 修改Redis中进度值
                String redisAudioKey = AudioConstants.PROGRESS_REDIS_KEY + userId + ":" + fileId;
                Object obj = redisUtil.get(redisAudioKey);
                long expireTime;
                if (obj != null) {
                    expireTime = redisUtil.getExpire(redisAudioKey);
                } else {
                    expireTime = AudioConstants.EXPIRE_DATE_30_MIN;
                }
                redisUtil.set(redisAudioKey, -1, expireTime);
            }
        }
        return null;
    }


    /**
     * 语音转写WEBapi方式-用异步回调
     * todo
     *
     * @param audioFilePath
     * @param userId
     * @return
     */
    public static LfasrMessage xfyunLfasrWebAPI(String audioFilePath, String userId, RedisUtil redisUtil, String fileId, BusUserMeetingAudioRepo audioRepo, Long audioId, String xfAppId, String xfLfasrSecretKey) {
        try {
//            audioFilePath = Objects.requireNonNull(MeetingAudioLongFromRsaScheduled.class.getResource("/")).toURI().getPath() + "/audio/lfasr.wav";
            // 1.上传
            String uploadRes = upload(audioFilePath, xfAppId, xfLfasrSecretKey);
            String jsonStr = StringEscapeUtils.unescapeJavaScript(uploadRes);
            String orderId = String.valueOf(JSONUtil.getByPath(JSONUtil.parse(jsonStr), "content.orderId"));
            log.info("转写任务 orderId：" + orderId);
            String redisAudioKey = AudioConstants.PROGRESS_REDIS_KEY + userId + ":" + fileId;
            log.info("转写任务 redisAudioKey：" + redisAudioKey);
            Object obj = redisUtil.get(redisAudioKey);
            long expireTime;
            if (obj != null) {
                expireTime = redisUtil.getExpire(redisAudioKey);
            } else {
                expireTime = AudioConstants.EXPIRE_DATE_30_MIN;
            }
            redisUtil.set(redisAudioKey, 30, expireTime);
            // 2.转写完成
            XFResultDTO result = getResult(orderId, xfAppId, xfLfasrSecretKey);
            if (result.getContent().getOrderInfo().getStatus() == 4) {
                // 更新语音文件表状态
                BusUserMeetingAudioEntity byId = audioRepo.getById(audioId);
                if (byId != null) {
                    byId.setOrderId(orderId);
                    byId.setIsTrans(AudioConstants.TRANS_SUCCESS);
                    audioRepo.updateById(byId);
                }
            } else if (result.getContent().getOrderInfo().getStatus() == -1) {
                // 更新语音文件表状态
                BusUserMeetingAudioEntity byId = audioRepo.getById(audioId);
                if (byId != null) {
                    byId.setOrderId(orderId);
                    byId.setIsTrans(AudioConstants.TRANS_FAIL);
                    audioRepo.updateById(byId);
                }
            } else {
                // 更新语音文件表状态
                BusUserMeetingAudioEntity byId = audioRepo.getById(audioId);
                if (byId != null) {
                    byId.setOrderId(orderId);
                    audioRepo.updateById(byId);
                }
            }
        } catch (Exception e) {
            log.error("转写异常");
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 上传语音文件到讯飞公有云接口
     *
     * @param audioFilePath
     * @return
     * @throws SignatureException
     * @throws FileNotFoundException
     */
    private static String upload(String audioFilePath, String xfAppId, String xfLfasrSecretKey) throws SignatureException, FileNotFoundException {
        HashMap<String, Object> map = new HashMap<>(16);
        File audio = new File(audioFilePath);
        String fileName = audio.getName();
        long fileSize = audio.length();
        map.put("appId", xfAppId);
        map.put("fileSize", fileSize);
        map.put("fileName", fileName);
        map.put("duration", "200");
        LfasrSignature lfasrSignature = new LfasrSignature(xfAppId, xfLfasrSecretKey);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());
        // 回调地址
        map.put("callbackUrl", callbackUrl);
        String paramString = HttpUtils.parseMapToPathParam(map);
        log.info("语音文件上传参数：" + paramString);
        String url = HOST + "/v2/api/upload" + "?" + paramString;
        log.info("语音文件上传地址：" + url);
        String response = HttpUtils.iflyrecUpload(url, new FileInputStream(audio));
        log.info("语音文件上传响应：" + response);
        return response;
    }

    /***
     * 获取转写结果
     * @param orderId
     * @return
     * @throws SignatureException
     * @throws InterruptedException
     * @throws IOException
     */
    private static XFResultDTO getResult(String orderId, String xfAppId, String xfLfasrSecretKey) throws SignatureException, InterruptedException, IOException {
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("orderId", orderId);
        LfasrSignature lfasrSignature = new LfasrSignature(xfAppId, xfLfasrSecretKey);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());
        map.put("appId", xfAppId);
        map.put("resultType", "transfer,predict");
        String paramString = HttpUtils.parseMapToPathParam(map);
        String url = HOST + "/v2/api/getResult" + "?" + paramString;

        log.info("获取转写结果地址：" + url);
//        while (true) {
            String response = HttpUtils.iflyrecGet(url);
            XFResultDTO xfResultDTO = gson.fromJson(response, XFResultDTO.class);
            if (xfResultDTO.getContent().getOrderInfo().getStatus() == 4 || xfResultDTO.getContent().getOrderInfo().getStatus() == -1) {
                log.info("转写结果响应：" + response);
                return xfResultDTO;
            } else {
                log.info("进行中...，状态为：" + xfResultDTO.getContent().getOrderInfo().getStatus());
//                Thread.sleep(10000);
            }
//        }
        return xfResultDTO;
    }
}
