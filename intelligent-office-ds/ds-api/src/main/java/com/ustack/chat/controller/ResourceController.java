package com.ustack.chat.controller;

import com.ustack.chat.service.BusResourceManageService;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.validation.ValidGroup;
import com.ustack.resource.dto.BusResourceManageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author PingY
 * @Classname ResourceController
 * @Description TODO
 * @Date 2025/2/18
 * @Created by PingY
 */
@RestController
@RequestMapping("/api/v1/resource")
@Slf4j
@RequiredArgsConstructor
public class ResourceController {

    private final BusResourceManageService busResourceManageService;

    /**
     * 资源管理-左侧树结构
     *
     * @return 文档树结构列表
     */
    @GetMapping("/resourceTreeListLeft")
    public RestResponse resourceTreeListLeft() {
        return busResourceManageService.resourceTreeListLeft();
    }

    /**
     * 资源管理-单棵文件夹树
     *
     * @return 文档树结构列表
     */
    @GetMapping("/resourceSingleTree")
    public RestResponse resourceSingleTree(String category) {
        return busResourceManageService.resourceSingleTree(category);
    }

    /**
     * 资源管理-右侧列表
     *
     * @param name     文件/文件夹名称
     * @param parentId 父ID
     * @return 子集列表
     */
    @GetMapping("/resourceListRight")
    public RestResponse resourceListRight(String name, Integer parentId) {
        return busResourceManageService.resourceListRight(name, parentId);
    }

    /**
     * 判断上传权限
     *
     * @return 结果集
     */
    @PostMapping("/checkUploadAuth")
    public RestResponse checkUploadAuth(@RequestParam Integer id) {
        return busResourceManageService.checkUploadAuth(id);
    }

    /**
     * 保存文件/文件夹
     *
     * @param resourceManageDTO 文件/文件夹入参对象
     * @return 结果集
     */
    @PostMapping("/saveResource")
    public RestResponse saveResource(@RequestBody @Validated(value = {ValidGroup.Insert.class}) BusResourceManageDTO resourceManageDTO) {
        return busResourceManageService.saveResource(resourceManageDTO);
    }

    /**
     * 更新文件/文件夹
     *
     * @param resourceManageDTO 文件/文件夹入参对象
     * @return 结果集
     */
    @PostMapping("/updateResource")
    public RestResponse updateResource(@RequestBody @Validated(value = {ValidGroup.Update.class}) BusResourceManageDTO resourceManageDTO) {
        return busResourceManageService.updateResource(resourceManageDTO);
    }

    /**
     * 删除文件/文件夹
     *
     * @return 结果集
     */
    @PostMapping("/deleteById")
    public RestResponse deleteById(@RequestParam Integer id) {
        return busResourceManageService.deleteById(id);
    }


    /**
     * 资源管理-查看文档分段列表
     *
     * @param id 文档ID
     * @return  文档分段列表
     */
    @GetMapping("/resourceSegmentList")
    public RestResponse resourceSegmentList(@RequestParam Integer id) {
        return busResourceManageService.resourceSegmentList(id);
    }


    /**
     * 根据fileId更新有云documentId和batch
     *
     * @return
     */
    @PostMapping("/updateInfoFromDify")
    public RestResponse updateInfoFromDify(@RequestParam String fileId, @RequestParam String documentId, @RequestParam String batch) {
        return busResourceManageService.updateInfoFromDify(fileId, documentId, batch);
    }

    /**
     * 更新向量化状态
     *
     * @return
     */
    @PostMapping("/updateIndexStatus")
    public RestResponse updateIndexStatus(@RequestParam String documentId, @RequestParam String indexingStatus, @RequestParam String indexingStatusName) {
        return busResourceManageService.updateIndexStatus(documentId, indexingStatus, indexingStatusName);
    }

    /**
     * 移动节点
     *
     * @return
     */
    @PostMapping("/moveNode")
    public RestResponse moveNode(@RequestParam Integer operateId, @RequestParam Integer targetId, @RequestParam String operateType) {
        return busResourceManageService.moveNode(operateId, targetId, operateType);
    }

    /**
     * 更新父级
     *
     * @return
     */
    @PostMapping("/updateParentId")
    public RestResponse updateParentId(@RequestParam Integer id, @RequestParam Integer originParentId, @RequestParam Integer newParentId) {
        return busResourceManageService.updateParentId(id, originParentId, newParentId);
    }

    /**
     * 更新次序
     *
     * @return
     */
    @PostMapping("/updateSort")
    public RestResponse updateSort(@RequestBody List<BusResourceManageDTO> list) {
        return busResourceManageService.updateSort(list);
    }

    /**
     * 同步dify
     *
     * @return 结果集
     */
    @PostMapping("/syncDifyDocument")
    public RestResponse syncDifyDocument(@RequestParam String datasetId, @RequestParam Integer parentId) {
        return busResourceManageService.syncDifyDocument(datasetId, parentId);
    }

    /**
     * 获取文件向量化配置枚举
     *
     * @return 结果集
     */
    @PostMapping("/listEmbedConfig")
    public RestResponse listEmbedConfig() {
        return busResourceManageService.listEmbedConfig();
    }

    /**
     * 获取文件向量化配置枚举
     *
     * @return 结果集
     */
    @PostMapping("/getEmbedConfig")
    public RestResponse getEmbedConfig(@RequestParam String configCode) {
        return busResourceManageService.getEmbedConfig(configCode);
    }

    /**
     * 获取文件向量化配置
     *
     * @return 结果集
     */
    @PostMapping("/getResourceEmbedInfo")
    public RestResponse getResourceEmbedInfo(@RequestParam Integer resourceId) {
        return busResourceManageService.getResourceEmbedInfo(resourceId);
    }
}
