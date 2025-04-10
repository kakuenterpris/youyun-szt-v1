package com.thtf.chat.controller;

import com.thtf.chat.service.BusUserMeetingAudioService;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.meeting.dto.ProgressParamDTO;
import com.thtf.meeting.dto.UserMeetingAudioContentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author：PingY
 * Package：com.thtf.chat.controller
 * Project：intelligent-office-platform
 * Classname：MeetingMinuteController
 * Date：2025/3/25  10:55
 * Description:
 */
@RestController
@RequestMapping("/api/v1/meetingMinute")
@Slf4j
@RequiredArgsConstructor
@Validated
public class MeetingMinuteController {
    private final BusUserMeetingAudioService busUserMeetingAudioService;

    /**
     * 执行语音转文件操作
     *
     * @param audioContentDTO
     * @return
     */
    @PostMapping("/audioToChar")
    public RestResponse audioToChar(@RequestBody UserMeetingAudioContentDTO audioContentDTO) {
        return busUserMeetingAudioService.audioToChar(audioContentDTO);
    }


    /**
     * 转换重试
      * @param audioContentDTO
     * @return
     */
    @PostMapping("/transRetry")
    public RestResponse transRetry(@RequestBody UserMeetingAudioContentDTO audioContentDTO) {
        return busUserMeetingAudioService.transRetry(audioContentDTO);
    }

    /**
     * 查当前用户 语音文件列表
     *
     * @return
     */
    @GetMapping("/queryAudioFileList")
    public RestResponse queryAudioFileList(@RequestParam String queryParam) {
        return busUserMeetingAudioService.queryAudioFileList(queryParam);
    }

    /**
     * 查文件进度
     *
     * @param
     * @return
     */
    @PostMapping("/queryLongAsrProgress")
    public RestResponse queryLongAsrProgress(@RequestBody List<ProgressParamDTO> progressParamDTOS) {
        return busUserMeetingAudioService.queryLongAsrProgress(progressParamDTOS);
    }

    /**
     * 获取单个语音文件表数据
     *
     * @param contentId
     * @return
     */
    @GetMapping("/queryAudioFileAndContent")
    public RestResponse queryAudioFileAndContent(@RequestParam String contentId) {
        return busUserMeetingAudioService.queryAudioFileAndContent(contentId);
    }

    /**
     * 更新文件名称
     *
     * @param audioContentDTO
     * @return
     */
    @PostMapping("/updateAudioEntity")
    public RestResponse updateAudioEntity(@RequestBody UserMeetingAudioContentDTO audioContentDTO) {
        return busUserMeetingAudioService.updateAudioEntity(audioContentDTO);
    }

    /**
     * 编辑内容
     *
     * @param audioContentDTO
     * @return
     */
    @PostMapping("/updateContent")
    public RestResponse updateContent(@RequestBody UserMeetingAudioContentDTO audioContentDTO) {
        return busUserMeetingAudioService.updateContent(audioContentDTO);
    }

    /**
     * 查回收站文件列表
     *
     * @return
     */
    @GetMapping("/queryAudioFileRecycleList")
    public RestResponse queryAudioFileRecycleList(@RequestParam String queryParam) {
        return busUserMeetingAudioService.queryAudioFileRecycleList(queryParam);
    }

    /**
     * 删除文件-逻辑删除
     *
     * @param id
     * @return
     */
    @PostMapping("/logicDelete")
    public RestResponse logicDelete(@RequestParam String id) {
        return busUserMeetingAudioService.logicDelete(id);
    }

    /**
     * 永久删除
     *
     * @param id
     * @return
     */
    @PostMapping("/permanentlyDelete")
    public RestResponse permanentlyDelete(@RequestParam String id) {
        return busUserMeetingAudioService.permanentlyDelete(id);
    }

    /**
     * 清空回收站
     * @return
     */
    @PostMapping("/clearRecycle")
    public RestResponse clearRecycle() {
        return busUserMeetingAudioService.clearRecycle();
    }

    /**
     * 文件还原
     * @param audioContentDTO
     * @return
     */
    @PostMapping("/fileRestore")
    public RestResponse fileRestore(@RequestBody UserMeetingAudioContentDTO audioContentDTO) {
        return busUserMeetingAudioService.fileRestore(audioContentDTO);
    }


    /**
     * 讯飞转写完成异步回调接口
     * todo
     *
     * @param orderId
     * @param status
     * @return
     */
    @GetMapping("/xfWebApiCallback")
    public RestResponse xfWebApiCallback(@RequestParam String orderId, int status) {
        return busUserMeetingAudioService.xfWebApiCallback(orderId, status);
    }


}
