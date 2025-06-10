package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.BusUserMeetingAudioEntity;
import com.ustack.chat.entity.BusUserMeetingContentEntity;
import com.ustack.chat.mapper.BusUserMeetingContentMapper;
import com.ustack.chat.repo.BusUserMeetingContentRepo;
import com.ustack.chat.service.BusUserMeetingContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Admin_14104
 * @description 针对表【bus_user_meeting_content(用户会议纪要-语音内容表)】的数据库操作Service实现
 * @createDate 2025-03-25 11:38:14
 */
@Service
@RequiredArgsConstructor
public class BusUserMeetingContentRepoImpl extends ServiceImpl<BusUserMeetingContentMapper, BusUserMeetingContentEntity>
        implements BusUserMeetingContentRepo {

    private final BusUserMeetingContentMapper contentMapper;

    @Override
    public Long add(BusUserMeetingContentEntity contentEntity) {
        this.save(contentEntity);
        return contentEntity.getId();
    }

    @Override
    public boolean logicDelete(Long contentId) {
        return lambdaUpdate()
                .set(BusUserMeetingContentEntity::getIsDeleted, true)
                .eq(BusUserMeetingContentEntity::getId, contentId)
                .update(new BusUserMeetingContentEntity());
    }

    @Override
    public boolean deleteById(Long id) {
        return contentMapper.completelyDelete(id);

    }

    @Override
    public boolean restore(Long contentId) {
       return contentMapper.restore(contentId);
    }

    @Override
    public BusUserMeetingContentEntity getByOrderId(String orderId) {
        return contentMapper.getByOrderId(orderId);
    }
}




