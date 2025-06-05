package com.thtf.op.controller;

import com.thtf.global.common.rest.RestResponse;
import com.thtf.op.entity.KrmDepartmentEntity;
import com.thtf.op.repo.KrmDepartmentRepo;
import com.thtf.resource.dto.KrmDepartmentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public RestResponse getKnowledgeType(@RequestBody KrmDepartmentDTO dto) {

        List<KrmDepartmentEntity> knowledgeType = krmDepartmentRepo.getTreeList(dto);
        return RestResponse.success(knowledgeType);
    }

    /**
     * 同步体系接口
     *
     * @param sysId 体系分类编码   1：学科体系
     *              2：技术体系
     *              3：业务体系
     *              4：部门体系
     *              5：领域体系
     * @return
     */
    @PostMapping("/syncKnowledgeType")
    public RestResponse syncKnowledgeType(@RequestBody KrmDepartmentDTO dto) throws IOException {


        return RestResponse.success(krmDepartmentRepo.syncKnowledgeType(dto));
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
    public RestResponse addKnowledgeType(@RequestParam(value = "sysId") String sysId,
                                         @RequestParam(value = "name") String name,
                                         @RequestParam(value = "pId") String pId) {
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

    @GetMapping("/delete")
    public RestResponse delete() {
        krmDepartmentRepo.delete();
        return RestResponse.success("删除成功");
    }
}
