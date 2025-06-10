package com.ustack.kbase.controller;

import com.ustack.global.common.rest.RestResponse;
import com.ustack.kbase.entity.CompanyVector;
import com.ustack.kbase.entity.DepartmentVector;
import com.ustack.kbase.entity.KmVector;
import com.ustack.kbase.entity.PersonalVector;
import com.ustack.kbase.service.KbaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年03月25日
 */
@RestController
@RequestMapping("/api/v1/kbase")
@Slf4j
@Tag(name = "kbase操作", description = "kbase相关接口")
public class KbaseController {

    @Autowired
    private KbaseService kbaseService;


    @PostMapping("/insert")
    @Operation(summary = "新增数据")
    public RestResponse insert(@RequestBody KmVector kmVector) {
        boolean bool = kbaseService.insert(kmVector);
        return RestResponse.success(bool);
    }

    @PostMapping("/deleteByFileId")
    @Operation(summary = "根据文件id删除企业知识库数据")
    public RestResponse deleteByFileId(@RequestParam String fileId) {
        boolean bool = kbaseService.deleteByFileId(fileId);
        return RestResponse.success(bool);
    }

    @PostMapping("/deleteByFolderId")
    @Operation(summary = "根据文件id删除企业知识库数据")
    public RestResponse deleteByFolderId(@RequestParam String folderId) {
        boolean bool = kbaseService.deleteByFolderId(folderId);
        return RestResponse.success(bool);
    }

    @PostMapping("/queryByEmbedding")
    @Operation(summary = "根据文件ID查询相关文档")
    public RestResponse queryByEmbedding(@RequestParam List<String> fileIdList, @RequestParam String embedding) {
        Map list = kbaseService.queryByEmbedding(fileIdList, embedding);
        return RestResponse.success(list);
    }

    @PostMapping("/queryByEmbeddingAndFolderIds")
    @Operation(summary = "根据文件夹查询相关文档")
    public RestResponse queryByEmbeddingAndFolderIds(@RequestParam List<String> folderIdIdList, @RequestParam String embedding) {
        Map list = kbaseService.queryByEmbeddingAndFolderIds(folderIdIdList, embedding);
        return RestResponse.success(list);
    }
    @PostMapping("/updateJoinValid")
    @Operation(summary = "修改是否参与问答")
    public RestResponse updateJoinValid(@RequestParam List<String> fileIdList, @RequestParam Integer joinValue) {
        boolean bool = kbaseService.updateJoinValid(fileIdList, joinValue);
        return RestResponse.success(bool);
    }

    @PostMapping("/insertCompany")
    @Operation(summary = "新增企业数据")
    public RestResponse insertCompany(@RequestBody CompanyVector companyVector) {
        boolean bool = kbaseService.insertCompany(companyVector);
        return RestResponse.success(bool);
    }

    @PostMapping("/insertDepartment")
    @Operation(summary = "新增部门数据")
    public RestResponse insertDepartment(@RequestBody DepartmentVector departmentVector) {
        boolean bool = kbaseService.insertDepartment(departmentVector);
        return RestResponse.success(bool);
    }

    @PostMapping("/insertPersonal")
    @Operation(summary = "新增个人数据")
    public RestResponse insertPersonal(@RequestBody PersonalVector personalVector) {
        boolean bool = kbaseService.insertPersonal(personalVector);
        return RestResponse.success(bool);
    }

    @PostMapping("/deleteCompanyByFileId")
    @Operation(summary = "根据文件id删除企业知识库数据")
    public RestResponse deleteCompany(@RequestParam String fileId) {
        boolean bool = kbaseService.deleteCompany(fileId);
        return RestResponse.success(bool);
    }
    @PostMapping("/deleteDepartmentByFileId")
    @Operation(summary = "根据文件id删除部门知识库数据")
    public RestResponse deleteDepartment(@RequestParam String fileId) {
        boolean bool = kbaseService.deleteDepartment(fileId);
        return RestResponse.success(bool);
    }
    @PostMapping("/deletePersonalByFileId")
    @Operation(summary = "根据文件id删除个人知识库数据")
    public RestResponse deletePersonal(@RequestParam String fileId) {
        boolean bool = kbaseService.deletePersonal(fileId);
        return RestResponse.success(bool);
    }

    @PostMapping("/queryPersonalByEmbedding")
    @Operation(summary = "查询个人相关文档")
    public RestResponse queryPersonalByEmbedding(String embedding, String userId) {
        Map list = kbaseService.queryPersonalByEmbedding(userId, embedding);
        return RestResponse.success(list);
    }

    @PostMapping("/queryDepartmentByEmbedding")
    @Operation(summary = "查询部门相关文档")
    public RestResponse queryDepartmentByEmbedding(String embedding, String depNum) {
        Map list = kbaseService.queryDepartmentByEmbedding(depNum, embedding);
        return RestResponse.success(list);
    }

    @PostMapping("/queryCompanyByEmbedding")
    @Operation(summary = "查询企业相关文档")
    public RestResponse queryCompanyByEmbedding(String embedding, String companyNum) {
        Map list = kbaseService.queryCompanyByEmbedding(companyNum, embedding);
        return RestResponse.success(list);
    }
}
