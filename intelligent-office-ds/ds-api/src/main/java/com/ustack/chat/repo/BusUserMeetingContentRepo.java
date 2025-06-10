package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.BusUserMeetingContentEntity;

/**
 * @author Admin_14104
 * @description 针对表【bus_user_meeting_content(用户会议纪要-语音内容表)】的数据库操作Service
 * @createDate 2025-03-25 11:38:14
 */
public interface BusUserMeetingContentRepo extends IService<BusUserMeetingContentEntity> {

    Long add(BusUserMeetingContentEntity contentEntity);

    boolean logicDelete(Long contentId);

    boolean deleteById(Long contentId);

    boolean restore(Long contentId);

    BusUserMeetingContentEntity getByOrderId(String orderId);
}
