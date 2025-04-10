package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.entity.*;
import com.thtf.chat.mapper.BusUserMeetingAudioMapper;
import com.thtf.chat.mappings.BusUserMeetingAudioMapping;
import com.thtf.chat.repo.BusUserMeetingAudioRepo;
import com.thtf.global.common.utils.Linq;
import com.thtf.meeting.dto.UserMeetingAudioContentDTO;
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
    public List<UserMeetingAudioContentDTO> getAudioList(String userId, String queryParam) {
        return audioMapper.getAudioList(userId, queryParam);
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
    public List<UserMeetingAudioContentDTO> getAudioRecycleList(String userId, String queryParam) {
        return audioMapper.getAudioRecycleList(userId, queryParam);
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
}




