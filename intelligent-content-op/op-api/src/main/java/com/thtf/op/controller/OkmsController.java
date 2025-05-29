package com.thtf.op.controller;

import com.thtf.global.common.rest.RestResponse;
import com.thtf.op.entity.KrmDepartmentEntity;
import com.thtf.op.repo.KrmDepartmentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/krm")
@Slf4j
public class OkmsController {

    @Autowired
    private KrmDepartmentRepo krmDepartmentRepo;


    /**
     * 查询体系接口
     * @param sysId  体系分类编码   1：学科体系
     * 2：技术体系
     * 3：业务体系
     * 4：部门体系
     * 5：领域体系
     * @return
     */
    @PostMapping("/getKnowledgeType")
    public RestResponse getKnowledgeType(@RequestParam(value = "sysId") String sysId){

        List<KrmDepartmentEntity> krmDepartmentEntityList = krmDepartmentRepo.getKnowledgeType(sysId);
        return RestResponse.success(krmDepartmentEntityList);
    }


    /**
     * 新增体系接口
     * @param sysId  体系分类编码   1：学科体系
     * 2：技术体系
     * 3：业务体系
     * 4：部门体系
     * 5：领域体系
     * @param name   体系节点名称   核数据与核数据评价
     * @param pId    父节点编码     0001
     * @return
     *
     *
     */
    @PostMapping("/addKnowledgeType")
    public RestResponse addKnowledgeType(String sysId, String name, String pId){
        int result = krmDepartmentRepo.addKnowledgeType(sysId,name,pId);
        return RestResponse.success(result);
    }


    /**
     * 修改体系接口
     *
     *  @param sysId  体系分类编码   1：学科体系
     * 2：技术体系
     * 3：业务体系
     * 4：部门体系
     * 5：领域体系
     * @param name   体系节点名称   核数据与核数据评价
     * @param pId    父节点编码     0001
     * @param id     当前节点编码   00010008
     * @return
     */
    @PostMapping("/updateKnowledgeType")
    public RestResponse updateKnowledgeType(String sysId, String name, String pId,String id){
        int result = krmDepartmentRepo.updateKnowledgeType(sysId, name, pId, id);
        return RestResponse.success(result);
    }


    /**
     * 删除体系接口
     * @param sysId  体系分类编码   1：学科体系
     * 2：技术体系
     * 3：业务体系
     * 4：部门体系
     * 5：领域体系
     * @param id     当前节点编码   00010008
     * @return
     */

    @GetMapping("/deleteKnowledgeType")
    public RestResponse deleteKnowledgeType(String sysId, String id){
        int result = krmDepartmentRepo.deleteKnowledgeType(sysId, id);
        return RestResponse.success(result);
    }
}
