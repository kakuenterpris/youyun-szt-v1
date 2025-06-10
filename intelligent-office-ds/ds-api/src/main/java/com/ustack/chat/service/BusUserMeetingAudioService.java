package com.ustack.chat.service;

import com.ustack.chat.entity.BusUserMeetingAudioEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.meeting.dto.ProgressParamDTO;
import com.ustack.meeting.dto.UserMeetingAudioContentDTO;

import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【bus_user_meeting_audio(用户会议纪要-语音文件表)】的数据库操作Service
 * @createDate 2025-03-25 11:28:49
 */
public interface BusUserMeetingAudioService extends IService<BusUserMeetingAudioEntity> {

    RestResponse audioToChar(UserMeetingAudioContentDTO audioContentDTO);

    RestResponse transRetry(UserMeetingAudioContentDTO audioContentDTO);

    RestResponse queryAudioFileList(String queryParam, Integer start, Integer size);

    RestResponse queryLongAsrProgress(List<ProgressParamDTO> progressParamDTOS);

    RestResponse queryAudioFileAndContent(String contentId);

    RestResponse updateAudioEntity(UserMeetingAudioContentDTO audioContentDTO);

    RestResponse updateContent(UserMeetingAudioContentDTO audioContentDTO);

    RestResponse logicDelete(String id);

    RestResponse permanentlyDelete(String id);

    RestResponse clearRecycle();

    RestResponse fileRestore(UserMeetingAudioContentDTO audioContentDTO);

    RestResponse queryAudioFileRecycleList(String queryParam, Integer start, Integer size);

    RestResponse xfWebApiCallback(String orderId, int status);
}
