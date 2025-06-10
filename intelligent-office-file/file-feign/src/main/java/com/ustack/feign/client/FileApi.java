package com.ustack.feign.client;

import com.ustack.feign.config.FileFeignAutoconfiguration;
import com.ustack.file.dto.FileUploadRecordBaseDTO;
import com.ustack.file.dto.SyncFileDTO;
import com.ustack.global.common.rest.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author：linxin
 * @ClassName: FileApi
 * @Date: 2025-02-20 10:11
 */
//@FeignClient(name = "file-api", url = "http://localhost:40000/", configuration = FileFeignAutoconfiguration.class)
@FeignClient(name = "file-api", configuration = FileFeignAutoconfiguration.class)
public interface FileApi {

    /**
     * 文件同步接口
     *
     * @return RestResponse
     * @param: dto
     * @author linxin
     * @date 2025/02/20 18:18
     */
    @PostMapping("/api/v1/file/syncDocument")
    RestResponse syncDocument(@RequestBody SyncFileDTO dto);

    /**
     * token验证
     *
     * @return RestResponse
     * @param: dto
     * @author linxin
     * @date 2025/02/20 17:26
     */
    @PostMapping("/api/v1/file/deleteDocument")
    RestResponse deleteDocument(@RequestBody SyncFileDTO dto);

    /**
     * 根据fileId获取文件信息
     *
     * @param fileId
     * @return
     */
    @PostMapping("/api/v1/file/getByFileId")
    RestResponse getByFileId(@RequestParam String fileId);

    /**
     * 获取文件
     *
     * @param fileId
     * @return
     */
    @PostMapping("/api/v1/file/getAudioFileByFileId")
    RestResponse getAudioFileByFileId(@RequestParam String fileId);

    /**
     * 删除服务器文件-上传记录表文件
     *
     * @param fileId
     * @return
     */
    @PostMapping("/api/v1/file/deleteFileCommon")
    RestResponse deleteFileCommon(@RequestParam String fileId);


}
