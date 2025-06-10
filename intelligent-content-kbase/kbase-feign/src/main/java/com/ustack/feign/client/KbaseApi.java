package com.ustack.feign.client;

import com.ustack.feign.config.KbaseFeignAutoconfiguration;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.kbase.entity.CompanyVector;
import com.ustack.kbase.entity.DepartmentVector;
import com.ustack.kbase.entity.KmVector;
import com.ustack.kbase.entity.PersonalVector;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "kbase-api", configuration = KbaseFeignAutoconfiguration.class)
public interface KbaseApi {


    @PostMapping("/api/v1/kbase/insertCompany")
    RestResponse insertCompany(@RequestBody CompanyVector companyVector);

    @PostMapping("/api/v1/kbase/insertDepartment")
    RestResponse insertDepartment(@RequestBody DepartmentVector departmentVector);

    @PostMapping("/api/v1/kbase/insertPersonal")
    RestResponse insertPersonal(@RequestBody PersonalVector personalVector);

    @PostMapping("/api/v1/kbase/queryPersonalByEmbedding")
    RestResponse queryPersonalByEmbedding(@RequestParam String embedding, @RequestParam String userId);

    @PostMapping("/api/v1/kbase/queryDepartmentByEmbedding")
    RestResponse queryDepartmentByEmbedding(@RequestParam("embedding") String embedding, @RequestParam("depNum") String depNum);

    @PostMapping("/api/v1/kbase/queryCompanyByEmbedding")
    RestResponse queryCompanyByEmbedding(@RequestParam String embedding, @RequestParam String companyNum);

    @PostMapping("/api/v1/kbase/deleteCompanyByFileId")
    RestResponse deleteCompanyByFileId(@RequestParam String fileId);

    @PostMapping("/api/v1/kbase/deleteDepartmentByFileId")
    RestResponse deleteDepartmentByFileId(@RequestParam String fileId);

    @PostMapping("/api/v1/kbase/deletePersonalByFileId")
    RestResponse deletePersonalByFileId(@RequestParam String fileId);

    @PostMapping("/api/v1/kbase/insert")
    RestResponse insert(@RequestBody KmVector kmVector);

    @PostMapping("/api/v1/kbase/deleteByFileId")
    RestResponse deleteByFileId(@RequestParam String fileId);

    @PostMapping("/api/v1/kbase/deleteByFolderId")
    RestResponse deleteByFolderId(@RequestParam String folderId);

    @PostMapping("/api/v1/kbase/queryByEmbedding")
    RestResponse queryByEmbedding(@RequestParam List<String> fileIdList, @RequestParam String embedding);
    @PostMapping("/api/v1/kbase/queryByEmbeddingAndFolderIds")
    RestResponse queryByEmbeddingAndFolderIds(@RequestParam List<String> folderIdIdList, @RequestParam String embedding);

    @PostMapping("/api/v1/kbase/updateJoinValid")
    RestResponse updateJoinValid(@RequestParam List<String> fileIdList, @RequestParam Integer joinValue);
}