package com.ustack.feign.client;

import com.ustack.feign.config.FileFeignAutoconfiguration;
import com.ustack.file.dto.SyncFileDTO;
import com.ustack.global.common.rest.RestResponse;
import lombok.SneakyThrows;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: 统一登录feign client
 * @author：linxin
 * @ClassName: FileApi
 * @Date: 2025-02-20 10:11
 */
//@FeignClient(name = "file-api", url = "http://localhost:40000/", configuration = FileFeignAutoconfiguration.class)
@FeignClient(name = "km-file-api", configuration = FileFeignAutoconfiguration.class)
public interface FileApi {

    /**
     * 文件上传接口 todo 增加文件更新逻辑，重新上传附带上旧的文件id
     *
     * @return RestResponse
     * @param: file
     * @param: fileMd5
     * @param: path
     * @author linxin
     * @date 2025/2/18 17:14
     */
    @PostMapping(value = "/api/v1/file/uploadFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    @SneakyThrows
    RestResponse updateFile(@RequestPart(value = "file") MultipartFile file,  // 文件
                            @RequestParam(value = "fileMd5", required = false) String fileMd5, // 文件md5
                            @RequestParam(value = "fileId", required = false) String fileId,  // 文件id（重新上传的文件保持旧的id）
                            @RequestParam(value = "path", required = false) String path);

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
