package com.thtf.chat.mappings;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thtf.chat.entity.BusUserMeetingAudioEntity;
import com.thtf.meeting.dto.UserMeetingAudioContentDTO;
import org.mapstruct.Mapper;

/**
* @author Admin_14104
* @description 针对表【bus_user_meeting_audio(用户会议纪要-语音文件表)】的数据库操作Mapper
* @createDate 2025-03-25 11:28:49
* @Entity com.thtf.chat.entity.BusUserMeetingAudioEntity
*/
@Mapper(componentModel = "spring")
public interface BusUserMeetingAudioMapping{

    BusUserMeetingAudioEntity dto2Entity(UserMeetingAudioContentDTO param);

    UserMeetingAudioContentDTO entity2Dto(BusUserMeetingAudioEntity param);

}




