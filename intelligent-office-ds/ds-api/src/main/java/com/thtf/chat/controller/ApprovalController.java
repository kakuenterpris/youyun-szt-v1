package com.thtf.chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thtf.chat.dto.ApproveDTO;
import com.thtf.chat.entity.FolderAuthEntity;
import com.thtf.chat.entity.SysAuthApprovalEntity;
import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.chat.repo.FolderAuthRepo;
import com.thtf.chat.repo.SysAuthApprovalRepo;
import com.thtf.global.common.rest.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/approve")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ApprovalController {
    @Autowired
    private SysAuthApprovalRepo sysAuthApprovalRepo;






//    获取审批列表
    @PostMapping("")
    public RestResponse approve(Page<SysAuthApprovalEntity> page){

//        ArrayList<FolderAuthEntity> folderAuthEntities = new ArrayList<>();
//        FolderAuthEntity folderAuthEntity = new FolderAuthEntity();
//        folderAuthEntity.setFolderId(1);
//        folderAuthEntity.setRoleId(1);
//        folderAuthEntity.setAuthType(1);
//        folderAuthEntities.add(folderAuthEntity);
//        //json字符串转换成对象
//        SysAuthApprovalEntity sysAuthApproval = new SysAuthApprovalEntity();
//        sysAuthApproval.getFolderAuthList();
//        sysAuthApproval.setFolderAuthList();
//        boolean save = sysAuthApprovalRepo.save(sysAuthApproval);
//        Page<SysAuthApprovalEntity> page1 = sysAuthApprovalRepo.page(page);
        return RestResponse.success("page1");
    }

    //todo通过审批
    @PostMapping("/operate")
    @Transactional(rollbackFor = Exception.class)
    public RestResponse approveDispose(@RequestBody ApproveDTO approveDTO){
        return sysAuthApprovalRepo.approveDispose(approveDTO);

    }


}
