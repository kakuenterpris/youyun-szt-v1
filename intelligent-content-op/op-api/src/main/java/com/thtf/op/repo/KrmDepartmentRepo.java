package com.thtf.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.op.entity.KrmDepartmentEntity;
import com.thtf.resource.dto.KrmDepartmentDTO;

import java.io.IOException;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【KRM_DEPARTMENT(数聚平台部门体系)】的数据库操作Service
* @createDate 2025-05-27 17:57:55
*/
public interface KrmDepartmentRepo extends IService<KrmDepartmentEntity> {

    List<KrmDepartmentEntity> getTreeList(KrmDepartmentDTO dto);

    int addKnowledgeType(String sysId, String name, String pId);

    int updateKnowledgeType(String sysId, String name, String pId, String id);

    int deleteKnowledgeType(String sysId, String id);

    void delete();

    RestResponse syncKnowledgeType(KrmDepartmentDTO dto) throws IOException;
}
