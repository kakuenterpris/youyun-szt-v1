package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.BusUserMeetingAudioEntity;
import com.ustack.meeting.dto.UserMeetingAudioContentDTO;

import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【bus_user_meeting_audio(用户会议纪要-语音文件表)】的数据库操作Service
 * @createDate 2025-03-25 11:28:49
 */
public interface BusUserMeetingAudioRepo extends IService<BusUserMeetingAudioEntity> {

    Integer getMaxSort();

    List<UserMeetingAudioContentDTO> getAudioList(String userId, String queryParam, Integer start, Integer size);

    UserMeetingAudioContentDTO getEntity(Long id);
    UserMeetingAudioContentDTO getDeletedEntity(Long id);

    List<UserMeetingAudioContentDTO> getAudioRecycleList(String userId, String queryParam, Integer start, Integer size);

    boolean logicDelete(Long id);

    boolean deleteById(Long id);

    int clearRecycle(String userId);

    boolean restore(Long id);

    int updateByIsTrans(Long id);

    BusUserMeetingAudioEntity getByOrderId(String orderId);

    BusUserMeetingAudioEntity getByFileId(String fileId, String userId);
}
