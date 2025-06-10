package com.ustack.op.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.validation.ValidGroup;
import com.ustack.op.annotation.RequiresPermission;
import com.ustack.op.service.KmService;
import com.ustack.resource.dto.*;
import com.ustack.resource.param.SaveFileParam;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author PingY
 * @Classname ResourceController
 * @Description TODO
 * @Date 2025/2/18
 * @Created by PingY
 */
@RestController
@RequestMapping("/api/v1/km")
@Slf4j
@RequiredArgsConstructor
public class KmController {

    private final KmService service;

    /**
     * 资源管理-左侧树结构
     *
     * @return 文档树结构列表
     */
    @RequiresPermission(value = "knowledgeBase", authtype = 0)
    @GetMapping("/resourceTreeListLeft")
    public RestResponse resourceTreeListLeft() {
        return service.resourceTreeListLeft();
    }

    /**
     * 资源管理-右侧列表
     *
     * @param query     query
     * @return 子集列表
     */
    @PostMapping("/resourceListRight")
    @RequiresPermission(value = "knowledgeBase", authtype = 0)
    public RestResponse resourceListRight(@RequestBody QueryDTO query) {
        try {
            RestResponse restResponse = service.resourceListRight(query);
            return restResponse;
        }catch (Exception e) {
            log.error("资源管理-右侧列表查询失败", e);
            return RestResponse.fail(RestResponse.ERROR_CODE,"资源管理-右侧列表查询失败");
        }

    }

    /**
     * 资源管理-更新是否参与问答
     *
     * @param query     query
     * @return 结果
     */
    @PostMapping("/updateJoinQuery")
    public RestResponse updateJoinQuery(@RequestBody QueryDTO query) {
        return service.updateJoinQuery(query);
    }

    /**
     * 资源管理-获取文件年度列表
     *
     * @return 文件年度列表
     */
    @GetMapping("/getFileYearList")
    public RestResponse getFileYearList(@RequestParam(required = false) Integer id) {
        return RestResponse.success(Collections.singletonList(2025));
    }

    /**
     * 获取文件操作权限
     * @param id 文件夹id
     * @return
     */
    @PostMapping("/getAuth")
    public RestResponse getAuth(@RequestParam Integer id) {
        return service.getAuth(id);
    }

    /**
     * 获取文件夹成员
     * @param id 文件夹id
     * @return
     */
    @PostMapping("/getMember")
    public RestResponse getMember(@RequestParam Integer id) {
        return service.getMember(id);
    }

    /**
     * 保存文件夹
     *
     * @param resourceManageDTO 文件/文件夹入参对象
     * @return 结果集
     */
    @PostMapping("/saveFolder")
    @RequiresPermission(value = "knowledgeBase", authtype = 1)
    public RestResponse saveFolder(@RequestBody @Validated(value = {ValidGroup.Insert.class, ValidGroup.Update.class}) BusResourceFolderDTO dto) {
        return service.saveFolder(dto);
    }

    /**
     * 保存文件
     *
     * @param resourceManageDTO 文件/文件夹入参对象
     * @return 结果集
     */
    @PostMapping("/saveFile")
    @RequiresPermission(value = "knowledgeBase", authtype = 1)
    public RestResponse saveFile(@RequestBody @Validated(value = {ValidGroup.Insert.class, ValidGroup.Update.class}) SaveFileParam dto) {
        return service.saveFile(dto);
    }
    /**
     * 批量移动
     *
     * @param resourceManageDTO 文件/文件夹入参对象
     * @return 结果集
     */
    @PostMapping("/moveBachFile")
    @RequiresPermission(value = "knowledgeBase", authtype = 1)
    public RestResponse moveBachFile(@RequestBody @Validated(value = {ValidGroup.Insert.class, ValidGroup.Update.class}) SaveFileParam dto) {
        return service.moveBachFile(dto);
    }

    /**
     * 获取MD预览数据
     *
     * @return 结果集
     */
    @PostMapping("/getPreview")
    public RestResponse getPreview(@RequestParam Integer id) throws IOException {
        return service.getPreview(id);
    }

    /**
     * 检查文件夹是否为空
     *
     * @return 结果集
     */
    @PostMapping("/checkEmptyFolder")
    public RestResponse checkEmptyFolder(@RequestParam Integer id) {
        return service.checkEmptyFolder(id);
    }

    /**
     * 删除文件夹
     *
     * @return 结果集
     */
    @PostMapping("/deleteFolder")
    @RequiresPermission(value = "knowledgeBase", authtype = 1)
    public RestResponse deleteFolder(@RequestParam Integer id) {
        return service.deleteFolder(id);
    }

    /**
     * 删除文件
     *
     * @return 结果集
     */
    @PostMapping("/deleteFile")
    @RequiresPermission(value = "knowledgeBase", authtype = 1)
    public RestResponse deleteFile(@RequestParam Integer id) {
        return service.deleteFile(id);
    }


    /**
     * 资源管理-查看文档分段列表
     *
     * @param id 文档ID
     * @return 文档分段列表
     */
    @GetMapping("/resourceSegmentList")
    public RestResponse resourceSegmentList(@RequestParam Integer id) {
        return service.resourceSegmentNewList(id);
    }

    /**
     * 获取文件向量化配置枚举
     *
     * @return 结果集
     */
    @PostMapping("/listEmbedConfig")
    public RestResponse listEmbedConfig() {
        return service.listEmbedConfig();
    }

    /**
     * 获取文件向量化配置枚举
     *
     * @return 结果集
     */
    @PostMapping("/getEmbedConfig")
    public RestResponse getEmbedConfig(@RequestParam String configCode) {
        return service.getEmbedConfig(configCode);
    }

    /**
     * 获取文件向量化配置
     *
     * @return 结果集
     */
    @PostMapping("/getResourceEmbedInfo")
    public RestResponse getResourceEmbedInfo(@RequestParam String resourceGuid) {
        return service.getResourceEmbedInfo(resourceGuid);
    }

    @PostMapping("/moveFolder")
    public RestResponse moveFolder(@RequestBody BusResourceFolderDTO dto) {
        return service.moveFolder(dto);
    }

    /**
     * 移动节点
     *
     * @return
     */
    @PostMapping("/moveNode")
    public RestResponse moveNode(@RequestParam Integer operateId, @RequestParam Integer targetId, @RequestParam String operateType) {
        return service.moveNode(operateId, targetId, operateType);
    }

    /**
     * 更新父级
     *
     * @return
     */
    @PostMapping("/updateParentId")
    public RestResponse updateParentId(@RequestParam Integer id, @RequestParam Integer originParentId, @RequestParam Integer newParentId) {
        return service.updateParentId(id, originParentId, newParentId);
    }

    /**
     * 更新次序
     *
     * @return
     */
    @PostMapping("/updateSort")
    public RestResponse updateSort(@RequestBody List<BusResourceManageDTO> list) {
        return service.updateSort(list);
    }

    /**
     * 同步dify
     *
     * @return 结果集
     */
    @PostMapping("/syncDifyDocument")
    public RestResponse syncDifyDocument(@RequestParam String datasetId, @RequestParam Integer parentId) {
        return service.syncDifyDocument(datasetId, parentId);
    }
}
