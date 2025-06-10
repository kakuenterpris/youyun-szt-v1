package com.ustack.chat.mapper;

import com.ustack.chat.entity.BusUserMeetingContentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Admin_14104
* @description 针对表【bus_user_meeting_content(用户会议纪要-语音内容表)】的数据库操作Mapper
* @createDate 2025-03-25 11:38:14
* @Entity com.ustack.chat.entity.BusUserMeetingContentEntity
*/
public interface BusUserMeetingContentMapper extends BaseMapper<BusUserMeetingContentEntity> {

    boolean completelyDelete(Long id);

    boolean restore(Long id);

    BusUserMeetingContentEntity getByOrderId(String orderId);
}




