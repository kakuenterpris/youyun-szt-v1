package com.ustack.chat.mapper;

import com.ustack.chat.entity.BusUserMeetingAudioEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.meeting.dto.UserMeetingAudioContentDTO;

import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【bus_user_meeting_audio(用户会议纪要-语音文件表)】的数据库操作Mapper
 * @createDate 2025-03-25 11:28:49
 * @Entity com.ustack.chat.entity.BusUserMeetingAudioEntity
 */
public interface BusUserMeetingAudioMapper extends BaseMapper<BusUserMeetingAudioEntity> {

    List<UserMeetingAudioContentDTO> getAudioList(String userId, String queryParam, Integer start, Integer size);

    List<UserMeetingAudioContentDTO> getAudioRecycleList(String userId, String queryParam, Integer start, Integer size);

    UserMeetingAudioContentDTO getEntity(Long id);

    boolean completelyDelete(Long id);

    int clearRecycle(String userId);

    boolean restore(Long id);

    int updateByIsTrans(Long id);

    BusUserMeetingAudioEntity getByOrderId(String orderId);

    BusUserMeetingAudioEntity getByFileId(String fileId, String userId);

    UserMeetingAudioContentDTO getDeletedEntity(Long id);
}




