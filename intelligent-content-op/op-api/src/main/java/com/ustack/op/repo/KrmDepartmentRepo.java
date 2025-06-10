package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.op.entity.KrmDepartmentEntity;

import java.util.Map;

/**
* @author Lenovo
* @description 针对表【KRM_DEPARTMENT(数聚平台部门体系)】的数据库操作Service
* @createDate 2025-05-27 17:57:55
*/
public interface KrmDepartmentRepo extends IService<KrmDepartmentEntity> {

    Map<String, Object> getKnowledgeType(String sysId);

    int addKnowledgeType(String sysId, String name, String pId);

    int updateKnowledgeType(String sysId, String name, String pId, String id);

    int deleteKnowledgeType(String sysId, String id);

    void delete();
}
