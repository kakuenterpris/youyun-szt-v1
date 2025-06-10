package com.ustack.chat.mapper;

import com.ustack.chat.entity.RelUserResourceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.resource.dto.RelUserResourceDTO;

import java.util.List;

/**
* @author Admin_14104
* @description 针对表【rel_user_resource】的数据库操作Mapper
* @createDate 2025-02-19 12:18:15
* @Entity com.ustack.chat.entity.RelUserResourceEntity
*/
public interface RelUserResourceMapper extends BaseMapper<RelUserResourceEntity> {
    /**
     * 获取向量化未完成的文件信息
     * @return
     */
    List<RelUserResourceDTO> getIndexingList();
}




