package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.LikeOrDislikeEntity;
import com.ustack.chat.mapper.LikeOrDislikeMapper;
import com.ustack.chat.repo.LikeOrDislikeRepo;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author zhoufei
* @description 针对表【like_or_dislike】的数据库操作Service实现
* @createDate 2025-03-24 15:29:16
*/
@Service
public class LikeOrDislikeRepoImpl extends ServiceImpl<LikeOrDislikeMapper, LikeOrDislikeEntity>
    implements LikeOrDislikeRepo {

    @Override
    public RestResponse likeOrDislike(LikeOrDislikeEntity likeOrDislikeEntity) {
        String userId = ContextUtil.getUserId();
        if (userId == null) {
            return RestResponse.error("用户不存在或未登录");
        }
        LikeOrDislikeEntity entity = lambdaQuery().eq(LikeOrDislikeEntity::getUserId, userId)
                .eq(LikeOrDislikeEntity::getConversationId, likeOrDislikeEntity.getConversationId())
                .eq(LikeOrDislikeEntity::getMessageId, likeOrDislikeEntity.getMessageId()).one();
        if (entity == null) {
            likeOrDislikeEntity.setUserId(userId);
            entity.setDictId(likeOrDislikeEntity.getDictId());
            entity.setSuggestion(likeOrDislikeEntity.getSuggestion());
            return RestResponse.success(save(likeOrDislikeEntity));
        }
        entity.setLikeStatus(likeOrDislikeEntity.getLikeStatus());
        entity.setDictId(likeOrDislikeEntity.getDictId());
        entity.setSuggestion(likeOrDislikeEntity.getSuggestion());
        return RestResponse.success(updateById(entity));
    }

    @Override
    public RestResponse getLikeOrDislike(String conversationId) {
        String userId = ContextUtil.getUserId();
        if (userId == null) {
            return RestResponse.error("用户不存在或未登录");
        }
        List<LikeOrDislikeEntity> list = lambdaQuery().eq(LikeOrDislikeEntity::getUserId, userId)
                .eq(LikeOrDislikeEntity::getConversationId, conversationId).list();

        return RestResponse.success(list);
    }
}




