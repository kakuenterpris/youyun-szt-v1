package com.ustack.chat.scheduled;

import cn.xfyun.model.response.lfasr.LfasrMessage;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.ustack.chat.entity.BusUserMeetingAudioEntity;
import com.ustack.chat.entity.BusUserMeetingContentEntity;
import com.ustack.chat.repo.BusUserMeetingAudioRepo;
import com.ustack.chat.service.BusUserMeetingContentService;
import com.ustack.feign.client.FileApi;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.meeting.constants.AudioConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Author：PingY
 * Date：2025/3/25  14:34
 * Description: 讯飞语音文件转文字-非实时
 */
@Slf4j
public class MeetingAudioLongFromRsaScheduled implements Runnable {
    private static final String HOST = "https://raasr.xfyun.cn";
    private static final Gson gson = new Gson();

    private final String fileId;
    private final String xfAppId;
    private final String xfLfasrSecretKey;
    private final RedisUtil redisUtil;
    private final String userId;
    private final BusUserMeetingContentService contentService;
    private final BusUserMeetingAudioRepo audioRepo;
    private final Long contentId;
    private final Long audioId;
    private final FileApi fileApi;

    public MeetingAudioLongFromRsaScheduled(String fileId, String xfAppId, String xfLfasrSecretKey, RedisUtil redisUtil,
                                            String userId, BusUserMeetingContentService contentService, Long contentId,
                                            BusUserMeetingAudioRepo audioRepo, Long audioId, FileApi fileApi) {
        this.fileId = fileId;
        this.xfAppId = xfAppId;
        this.xfLfasrSecretKey = xfLfasrSecretKey;
        this.redisUtil = redisUtil;
        this.userId = userId;
        this.contentService = contentService;
        this.contentId = contentId;
        this.audioRepo = audioRepo;
        this.audioId = audioId;
        this.fileApi = fileApi;
    }

    @Override
    public void run() {
        try {
            RestResponse restResponse = fileApi.getAudioFileByFileId(fileId);
            String filePath = "";
            File file = null;
            if (restResponse.isSuccess()) {
                LinkedHashMap linkedHashMap = (LinkedHashMap) restResponse.getData();
                filePath = linkedHashMap.get("data") != null
                        && StringUtils.isNoneBlank(String.valueOf(linkedHashMap.get("data")))
                        ? String.valueOf(linkedHashMap.get("data")) : "";
            }
            // 执行转写
            LfasrMessage lfasrMessage;
            // 获取文件大小
            File audio = new File(filePath);
            long fileSize = audio.length();
            log.info("文件大小：" + fileSize);
            lfasrMessage = CommonTrans.xfyunLfasrSDK(filePath, userId, xfAppId, xfLfasrSecretKey, fileId, redisUtil, audioRepo, audioId);
            StringBuilder stringBuilder = new StringBuilder();
            long realDuration = 0L;
            if (lfasrMessage != null) {
                String data = lfasrMessage.getData();
                List<Map> jsonList = JSONArray.parseObject(data, List.class);
                for (int i = 0; i < jsonList.size(); i++) {
                    Map map = jsonList.get(i);
                    stringBuilder.append((map.get(AudioConstants.XF_RESULT_DATA_ONEBEST) != null
                            && StringUtils.isNoneBlank(String.valueOf(map.get(AudioConstants.XF_RESULT_DATA_ONEBEST))))
                            ? String.valueOf(map.get(AudioConstants.XF_RESULT_DATA_ONEBEST)) : "");
                    if (i + 1 == jsonList.size()) { // 最后一个片段，获取总时长
                        realDuration = Long.parseLong(String.valueOf(map.get(AudioConstants.XF_RESULT_DATA_ED)));
                    }
                }
            }
            // 根据audioId从bus_user_meeting_audio表获取orderId
            BusUserMeetingAudioEntity audioEntity = audioRepo.getById(audioId);
            String orderId = "";
            if (audioEntity != null) {
                orderId = audioEntity.getOrderId();
            }
            // 将执行内容更新到语音内容表中
            BusUserMeetingContentEntity contentEntity = new BusUserMeetingContentEntity(contentId, stringBuilder.toString());
            contentEntity.setOrderId(orderId);
            contentEntity.setRealDuration(realDuration);
            contentService.updateById(contentEntity);
            log.info("语音转写任务完成,已更新语音内容至bus_user_meeting_content表");
        } catch (Exception e) {
            log.error("语音转写任务失败,失败原因：" + e);
            throw new RuntimeException(e);
        }
    }

}
