package com.ustack.kbase.service;

import com.ustack.global.common.rest.RestResponse;
import com.ustack.kbase.entity.CompanyVector;
import com.ustack.kbase.entity.DepartmentVector;
import com.ustack.kbase.entity.KmVector;
import com.ustack.kbase.entity.PersonalVector;

import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年03月25日
 */
public interface KbaseService {

    boolean insertCompany(CompanyVector companyVector);

    boolean deleteCompany(String fileId);

    boolean deleteDepartment(String fileId);

    boolean deletePersonal(String fileId);

    Map selectCompanyExecute(String tableName, String condition, String companyId);

    Map selectPersonalExecute(String tableName, String condition, String useId);

    Map selectDepartmentExecute(String tableName, String condition, String departmentId);

    boolean insertDepartment(DepartmentVector departmentVector);

    boolean insertPersonal(PersonalVector personalVector);

    RestResponse getKnowledge(String query);

    Map queryPersonalByEmbedding(String userId, String embedding);

    Map queryDepartmentByEmbedding(String deptNum, String embedding);

    Map queryCompanyByEmbedding(String companyNum, String embedding);

    boolean insert(KmVector kmVector);

    boolean deleteByFileId(String fileId);

    Map queryByEmbedding(List<String> fileIdList, String embedding);

    Map queryByEmbeddingAndFolderIds(List<String> folderIdIdList, String embedding);

    boolean deleteByFolderId(String folderId);

    boolean updateJoinValid(List<String> fileIdList, Integer joinValue);
}
