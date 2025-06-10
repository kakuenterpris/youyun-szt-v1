package com.ustack.chat.service;

import com.ustack.chat.entity.RelUserResourceEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Admin_14104
* @description 针对表【rel_user_resource】的数据库操作Service
* @createDate 2025-02-19 12:18:15
*/
public interface RelUserResourceService {

    /**
     * 根据用户ID获取向量库id
     * @param userId
     * @return
     */
    String getDatasetIdByUserId(String userId);
}
