package com.ustack.chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.chat.dto.ApproveDTO;
import com.ustack.chat.entity.FolderAuthEntity;
import com.ustack.chat.entity.SysAuthApprovalEntity;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.repo.FolderAuthRepo;
import com.ustack.chat.repo.SysAuthApprovalRepo;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.Linq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("list")
    public RestResponse approve(Page<SysAuthApprovalEntity> page){
        return sysAuthApprovalRepo.approveList(page);
    }

    //todo通过审批
    @PostMapping("/operate")
    @Transactional(rollbackFor = Exception.class)
    public RestResponse approveDispose(@RequestBody ApproveDTO approveDTO){
        return sysAuthApprovalRepo.approveDispose(approveDTO);

    }

}
