package com.ustack.chat.controller;

import com.ustack.chat.service.BusUserMeetingAudioService;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.meeting.dto.ProgressParamDTO;
import com.ustack.meeting.dto.UserMeetingAudioContentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author：PingY
 * Package：com.ustack.chat.controller
 * Project：intelligent-office-platform
 * Date：2025/3/25  10:55
 * Description:
 */
@RestController
@RequestMapping("/api/v1/meetingMinute")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "会议记录", description = "会议记录相关接口")
public class MeetingMinuteController {
    private final BusUserMeetingAudioService busUserMeetingAudioService;

    /**
     * 执行语音转文件操作
     *
     * @param audioContentDTO
     * @return
     */
    @PostMapping("/audioToChar")
    @Operation(summary = "语音转文件接口")
    public RestResponse audioToChar(@RequestBody UserMeetingAudioContentDTO audioContentDTO) {
        return busUserMeetingAudioService.audioToChar(audioContentDTO);
    }


    /**
     * 转换重试
      * @param audioContentDTO
     * @return
     */
    @PostMapping("/transRetry")
    @Operation(summary = "转换重试接口")
    public RestResponse transRetry(@RequestBody UserMeetingAudioContentDTO audioContentDTO) {
        return busUserMeetingAudioService.transRetry(audioContentDTO);
    }

    /**
     * 查当前用户 语音文件列表
     *
     * @return
     */
    @GetMapping("/queryAudioFileList")
    @Operation(summary = "查当前用户语音文件列表接口")
    public RestResponse queryAudioFileList(@RequestParam String queryParam, @RequestParam(defaultValue = "1") Integer start, @RequestParam(defaultValue = "10") Integer size) {
        return busUserMeetingAudioService.queryAudioFileList(queryParam, start, size);
    }

    /**
     * 查文件进度
     *
     * @param
     * @return
     */
    @PostMapping("/queryLongAsrProgress")
    @Operation(summary = "查文件进度接口")
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
    @Operation(summary = "获取单个语音文件表数据接口")
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
    @Operation(summary = "更新文件名称接口")
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
    @Operation(summary = "编辑内容接口")
    public RestResponse updateContent(@RequestBody UserMeetingAudioContentDTO audioContentDTO) {
        return busUserMeetingAudioService.updateContent(audioContentDTO);
    }

    /**
     * 查回收站文件列表
     *
     * @return
     */
    @GetMapping("/queryAudioFileRecycleList")
    @Operation(summary = "查回收站文件列表接口")
    public RestResponse queryAudioFileRecycleList(@RequestParam String queryParam, @RequestParam(defaultValue = "1") Integer start, @RequestParam(defaultValue = "10") Integer size) {
        return busUserMeetingAudioService.queryAudioFileRecycleList(queryParam, start, size);
    }

    /**
     * 删除文件-逻辑删除
     *
     * @param id
     * @return
     */
    @PostMapping("/logicDelete")
    @Operation(summary = "删除文件-逻辑删除接口")
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
    @Operation(summary = "永久删除接口")
    public RestResponse permanentlyDelete(@RequestParam String id) {
        return busUserMeetingAudioService.permanentlyDelete(id);
    }

    /**
     * 清空回收站
     * @return
     */
    @PostMapping("/clearRecycle")
    @Operation(summary = "清空回收站接口")
    public RestResponse clearRecycle() {
        return busUserMeetingAudioService.clearRecycle();
    }

    /**
     * 文件还原
     * @param audioContentDTO
     * @return
     */
    @PostMapping("/fileRestore")
    @Operation(summary = "文件还原接口")
    public RestResponse fileRestore(@RequestBody UserMeetingAudioContentDTO audioContentDTO) {
        return busUserMeetingAudioService.fileRestore(audioContentDTO);
    }

}
