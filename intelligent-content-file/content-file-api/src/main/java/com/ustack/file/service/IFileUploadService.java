package com.ustack.file.service;


import com.ustack.file.dto.*;
import com.ustack.global.common.rest.RestResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @Description : 文件上传service
 * @Author : LinXin
 * @ClassName : FileUploadService
 * @Date: 2021-01-20 10:54
 */
@Service
public interface IFileUploadService {


    /**
     * 文件上传方法
     * @param file
     * @param uuid
     * @param chunk
     * @param fileMd5
     * @param fileName
     * @author linxin
     * @date 2022/6/22 14:02
     * @throws Exception
     */
    RestResponse uploadSliceFile(MultipartFile file, String uuid, Integer chunk, String fileMd5, String fileName) throws Exception;


    /**
     * 校验秒传和分片续传
     * @param path
     * @param uuid
     * @param chunk
     * @param fileMd5
     * @param fileName
     * @author linxin
     * @date 2022/7/8 11:00
     */
    RestResponse checkSliceFile(String path, String uuid, Integer chunk, String fileMd5, String fileName) throws Exception;

    /**
     * 文件删除
     * @param dto
     * @author linxin
     * @date 2021/3/24 13:47
     */
    RestResponse deleteFile(FileUploadRecordBaseDTO dto);

    /**
     * guid获取文件流
     * @param response
     * @param guid
     * @author linxin
     * @return void
     * @date 2021/8/10 09:04
     */
    void getFileStreamByGuid(HttpServletResponse response, String guid) throws UnsupportedEncodingException;

    /**
     * 文件上传成功,合并文件
     * @param uuid
     * @author linxin
     * @date 2022/6/23 10:55
     * throws Exception
     */
    RestResponse mergeSliceFile(String uuid, String fileMd5, String name, String path) throws Exception;

    /**
     * 单个文件上传
     * @param file
     * @param path
     * @author linxin
     * @date 2022/6/23 16:01
     */
    RestResponse uploadFile(MultipartFile file, String fileMd5, String fileId, String path) throws Exception;


    /**
     * 修改是否文件可下载
     * @param param
     * @author linxin
     * @date 2022/7/7 08:35
     */
    RestResponse enableDownload(FileUploadRecordBaseDTO param);


    /**
     * base64方式上传文件
     * @param dto
     * @author linxin
     * @date 2022/10/20 09:02
     * @throws Exception
     */
    RestResponse uploadBase64(FileUploadBase64DTO dto) throws Exception;

    /**
     * 根据类型批量查询
     * @param dto
     * @author linxin
     * @date 2022/11/3 09:11
     */
    RestResponse batchQuery(FileBatchQueryDTO dto);


    /**
     *
     * @param: dto
     * @author linxin
     * @return RestResponse
     * @date 2025/2/20 14:51
     */
    RestResponse syncDocument(SyncFileDTO dto);


    /**
     *
     * @param: dto
     * @author linxin
     * @return RestResponse
     * @date 2025/2/20 14:51
     */
    RestResponse checkCanUpload(SyncFileDTO dto) throws IOException;

    /**
     * 删除文档
     * @param: dto
     * @author linxin
     * @return RestResponse
     * @date 2025/2/20 14:56
     */
    RestResponse deleteDocument(SyncFileDTO dto);

    FileUploadRecordDTO getByFileId(String fileId);
}
