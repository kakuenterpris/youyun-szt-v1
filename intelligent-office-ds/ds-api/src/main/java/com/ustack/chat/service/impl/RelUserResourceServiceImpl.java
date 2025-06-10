package com.ustack.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.util.StringUtil;
import com.ustack.chat.entity.RelUserResourceEntity;
import com.ustack.chat.repo.RelUserResourceRepo;
import com.ustack.chat.service.RelUserResourceService;
import com.ustack.chat.mapper.RelUserResourceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【rel_user_resource】的数据库操作Service实现
 * @createDate 2025-02-19 12:18:15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RelUserResourceServiceImpl implements RelUserResourceService {

    @Autowired
    private RelUserResourceRepo relUserResourceRepo;

    /**
     * 根据用户名获取向量库id
     *
     * @param userId
     * @return
     */
    @Override
    public String getDatasetIdByUserId(String userId) {
        if (StringUtil.isEmpty(userId)) {
            return null;
        }
        List<RelUserResourceEntity> list = relUserResourceRepo.list(userId);
        if (null != list && list.size() > 0) {
            return list.get(0).getDatasetsId();
        }
        return null;
    }
}




