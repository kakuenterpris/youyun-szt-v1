package com.thtf.chat.mapper;

import com.thtf.chat.entity.BusUserMeetingAudioEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thtf.meeting.dto.UserMeetingAudioContentDTO;

import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【bus_user_meeting_audio(用户会议纪要-语音文件表)】的数据库操作Mapper
 * @createDate 2025-03-25 11:28:49
 * @Entity com.thtf.chat.entity.BusUserMeetingAudioEntity
 */
public interface BusUserMeetingAudioMapper extends BaseMapper<BusUserMeetingAudioEntity> {

    List<UserMeetingAudioContentDTO> getAudioList(String userId, String queryParam);

    List<UserMeetingAudioContentDTO> getAudioRecycleList(String userId, String queryParam);

    UserMeetingAudioContentDTO getEntity(Long id);

    boolean completelyDelete(Long id);

    int clearRecycle(String userId);

    boolean restore(Long id);

    int updateByIsTrans(Long id);
}




