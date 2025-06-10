package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.LikeOrDislikeEntity;
import com.ustack.global.common.rest.RestResponse;

/**
* @author zhoufei
* @description 针对表【like_or_dislike】的数据库操作Service
* @createDate 2025-03-24 15:29:16
*/
public interface LikeOrDislikeRepo extends IService<LikeOrDislikeEntity> {

    RestResponse likeOrDislike(LikeOrDislikeEntity likeOrDislikeEntity);

    RestResponse getLikeOrDislike(String agentAnswerId);
}
