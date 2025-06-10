package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.BusUserMeetingAudioEntity;
import com.ustack.chat.mapper.BusUserMeetingAudioMapper;
import com.ustack.chat.mappings.BusUserMeetingAudioMapping;
import com.ustack.chat.repo.BusUserMeetingAudioRepo;
import com.ustack.meeting.dto.UserMeetingAudioContentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【bus_user_meeting_audio(用户会议纪要-语音文件表)】的数据库操作Service实现
 * @createDate 2025-03-25 11:28:49
 */
@Service
@RequiredArgsConstructor
public class BusUserMeetingAudioRepoImpl extends ServiceImpl<BusUserMeetingAudioMapper, BusUserMeetingAudioEntity>
        implements BusUserMeetingAudioRepo {

    private final BusUserMeetingAudioMapper audioMapper;

    private final BusUserMeetingAudioMapping audioMapping;

    @Override
    public Integer getMaxSort() {
        List<BusUserMeetingAudioEntity> list = lambdaQuery()
                .orderByDesc(BusUserMeetingAudioEntity::getSort)
                .list();
        return list.isEmpty() ? 0 : list.get(0).getSort();
    }

    @Override
    public List<UserMeetingAudioContentDTO> getAudioList(String userId, String queryParam, Integer start, Integer size) {
        return audioMapper.getAudioList(userId, queryParam, start, size);
    }

    @Override
    public UserMeetingAudioContentDTO getEntity(Long id) {
//        List<BusUserMeetingAudioEntity> list = lambdaQuery()
//                .eq(BusUserMeetingAudioEntity::getId, id)
//                .list();
//        return Linq.select(list, audioMapping::entity2Dto);
        return audioMapper.getEntity(id);
    }

    @Override
    public UserMeetingAudioContentDTO getDeletedEntity(Long id) {
        return audioMapper.getDeletedEntity(id);
    }

    @Override
    public List<UserMeetingAudioContentDTO> getAudioRecycleList(String userId, String queryParam, Integer start, Integer size) {
        return audioMapper.getAudioRecycleList(userId, queryParam, start, size);
    }

    @Override
    public boolean logicDelete(Long id) {
        return lambdaUpdate()
                .set(BusUserMeetingAudioEntity::getIsDeleted, true)
                .eq(BusUserMeetingAudioEntity::getId, id)
                .update(new BusUserMeetingAudioEntity());
    }

    @Override
    public boolean deleteById(Long id) {
        return audioMapper.completelyDelete(id);
    }

    @Override
    public int clearRecycle(String userId) {
        return audioMapper.clearRecycle(userId);
    }

    @Override
    public boolean restore(Long id) {
        return audioMapper.restore(id);
    }

    @Override
    public int updateByIsTrans(Long id) {
        return audioMapper.updateByIsTrans(id);
    }

    @Override
    public BusUserMeetingAudioEntity getByOrderId(String orderId) {
        return audioMapper.getByOrderId(orderId);
    }

    @Override
    public BusUserMeetingAudioEntity getByFileId(String fileId, String userId) {
        return audioMapper.getByFileId(fileId, userId);
    }
}




