package com.ustack.file.controller;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.ustack.file.dto.FileUploadBase64DTO;
import com.ustack.file.dto.FileUploadRecordBaseDTO;
import com.ustack.file.dto.MergeFileDTO;
import com.ustack.file.dto.SyncFileDTO;
import com.ustack.file.service.impl.SystemPathFileUploadServiceImpl;
import com.ustack.global.common.rest.RestResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @Description : 文件上传，下载接口
 * @Author : LinXin
 * @ClassName : FileUploadController
 * @Date: 2021-01-12 11:41
 */
@RestController
@RequestMapping("/api/v1/file/")
@Slf4j
@CrossOrigin
public class FileUploadController {

    private static final String[] whiteList = {"TXT", "MARKDOWN", "MDX", "PDF", "HTML", "XLSX", "XLS", "DOCX", "CSV", "MD", "HTM",
            "txt", "markdown", "mdx", "pdf", "html", "xlsx", "xls", "docx", "csv", "md", "htm",
            "MP3", "WAV", "PCM", "AAC", "Opus", "FLAC", "OGG", "AMR", "Speex", "AC3", "APE", "M4A", "M4R", "WMA","MP4"};

    SystemPathFileUploadServiceImpl fileUploadService;

    @Autowired
    public FileUploadController(SystemPathFileUploadServiceImpl fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("fetchId")
    public RestResponse fetchUuid() {
        return RestResponse.success(IdUtil.simpleUUID());
    }


    @PostMapping("mergeFile")
    public RestResponse mergeFile(@RequestBody MergeFileDTO dto) throws Exception {

        return fileUploadService.mergeSliceFile(dto.getUuid(), dto.getFileMd5(), dto.getFileName(), dto.getPath());
    }


    /**
     * 分片文件上传接口
     *
     * @return RestResponse
     * @param: file
     * @param: uuid
     * @param: fileMd5
     * @param: fileName
     * @param: chunkNumber
     * @author linxin
     * @date 2025/2/18 17:56
     */
    @PostMapping(value = "uploadSlice")
    @ResponseBody
    @SneakyThrows
    public RestResponse uploadFile(@RequestParam(value = "file") MultipartFile file,
                                   @RequestParam(value = "uuid") String uuid,
                                   @RequestParam("fileMd5") String fileMd5,
                                   @RequestParam("filename") String fileName,
                                   @RequestParam(value = "chunkNumber") Integer chunkNumber
    ) {

        String suffix = FileNameUtil.getSuffix(file.getOriginalFilename());
        if (Arrays.stream(whiteList).noneMatch(s -> suffix.equalsIgnoreCase(s))) {
            return RestResponse.error(String.format("%s 文件不支持上传，如有特殊上传需求请联系管理员！", suffix));
        }
        return fileUploadService.uploadSliceFile(file, uuid, chunkNumber, fileMd5, fileName);
    }


    /**
     * 分片上传预查询
     *
     * @return RestResponse
     * @param: uuid
     * @param: fileMd5
     * @param: fileName
     * @param: chunkNumber
     * @param: enablePreview
     * @param: enableDownload
     * @param: path
     * @author linxin
     * @date 2025/2/18 17:55
     */
    @GetMapping(value = "uploadSlice")
    @ResponseBody
    @SneakyThrows
    public RestResponse checkSlice(@RequestParam(value = "uuid") String uuid,
                                   @RequestParam("fileMd5") String fileMd5,
                                   @RequestParam("filename") String fileName,
                                   @RequestParam(value = "chunkNumber") Integer chunkNumber,
                                   @RequestParam(value = "path", required = false) String path
    ) {

        return fileUploadService.checkSliceFile(path, uuid, chunkNumber, fileMd5, fileName);
    }

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
    @PostMapping(value = "uploadFile")
    @ResponseBody
    @SneakyThrows
    public RestResponse updateFile(@RequestParam(value = "file") MultipartFile file,  // 文件
                                   @RequestParam(value = "fileMd5", required = false) String fileMd5, // 文件md5
                                   @RequestParam(value = "fileId", required = false) String fileId,  // 文件id（重新上传的文件保持旧的id）
                                   @RequestParam(value = "path", required = false) String path) {    // 指定保存路径

//        String suffix = FileNameUtil.getSuffix(file.getOriginalFilename());
//        if (Arrays.stream(whiteList).noneMatch(s -> suffix.equalsIgnoreCase(s))) {
//            return RestResponse.error(String.format("%s 文件不支持上传，如有特殊上传需求请联系管理员！", suffix));
//        }
        // 限制上传文件大小不超过15MB
        if (file.getSize() > 300 * 1024 * 1024) {
            return RestResponse.error("文件大小超过 300MB，无法上传！");
        }
        return fileUploadService.uploadFile(file, fileMd5, fileId, path);
    }


    @PostMapping("deleteFile")
    public RestResponse deleteFile(@RequestBody FileUploadRecordBaseDTO dto) {

        return fileUploadService.deleteFile(dto);
    }


    @PostMapping("enableDownload")
    public RestResponse enableDownload(@RequestBody FileUploadRecordBaseDTO param) {

        return fileUploadService.enableDownload(param);
    }


    @GetMapping("download/{guid}")
    public void getFileStreamByGuid(HttpServletResponse response, @PathVariable String guid) throws UnsupportedEncodingException {
        fileUploadService.getFileStreamByGuid(response, guid);
    }

    @PostMapping("uploadBase64")
    public RestResponse uploadBase64(@RequestBody FileUploadBase64DTO dto) throws Exception {

        return fileUploadService.uploadBase64(dto);
    }


    @PostMapping("syncDocument")
    public RestResponse syncDocument(@RequestBody SyncFileDTO dto) {

        return fileUploadService.syncDocument(dto);
    }


    @PostMapping("checkCanUpload")
    public RestResponse checkCanUpload(@RequestBody SyncFileDTO dto) throws IOException {

        return fileUploadService.checkCanUpload(dto);
    }


    @PostMapping("deleteDocument")
    public RestResponse deleteDocument(@RequestBody SyncFileDTO dto) {

        return fileUploadService.deleteDocument(dto);
    }


    @PostMapping("getByFileId")
    public RestResponse getByFileId(@RequestParam String fileId) {
        return RestResponse.success(fileUploadService.getByFileId(fileId));
    }

    @PostMapping("getAudioFileByFileId")
    public RestResponse getAudioFileByFileId(@RequestParam String fileId) {
        return RestResponse.success(fileUploadService.getAudioFileByFileId(fileId));
    }


    /**
     * 删除文件上传表记录与服务器文件
     *
     * @param fileId = guid
     * @return
     */
    @PostMapping("deleteFileCommon")
    public RestResponse deleteFileCommon(@RequestParam String fileId) {
        return fileUploadService.deleteFileCommon(fileId);
    }
}
