package com.ustack.file.utils;

import com.ustack.file.dto.FileUploadRecordDTO;
import com.ustack.file.entity.FileUploadRecordEntity;
import com.ustack.file.enums.FileUploadTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author linxin
 * @Description : 拼接文件下载地址
 * @ClassName : FileDownloadUrlUtil
 * @Date: 2021-08-12 13:58
 */
public class FileDownloadUrlUtil {


    private static final String windowsSeparator = "\\";

    /**
     * 生成文件记录DTO，对路径加密
     * @param recordEntity 文档上传记录
     * @author linxin
     * @date 2022/6/22 13:36
     */
    public static FileUploadRecordDTO getUploadRecordDTO(FileUploadRecordEntity recordEntity) throws Exception {
        // 预览路径生成
        String path = resolvePath(recordEntity.getPath());
        String previewPath = new StringBuffer(path).append(FileNameUtil.getPrefix(recordEntity.getFileName())).append(File.separator).toString();
        // 处理过的路径
        recordEntity.setPath(path);
        return convertEntityToDTO(recordEntity, previewPath);
    }

    public static String resolvePath(String path){
        // 替换Windows格式的路劲
        if(StringUtils.isBlank(path)){
           return path;
        }
        if(path.contains(windowsSeparator)){
            path = File.separator + path;
        }
        return path.replace(windowsSeparator, File.separator);
    }


    public static FileUploadRecordDTO convertEntityToDTO(FileUploadRecordEntity recordEntity, String previewPath) {
        FileUploadRecordDTO resultDTO = FileUploadRecordDTO.builder()
                .guid(recordEntity.getGuid())
                .documentId(recordEntity.getDocumentId())
                .orderBy(recordEntity.getOrderBy())
                .suffix(recordEntity.getSuffix())
                .path(recordEntity.getPath())
                .previewPath(previewPath)
                .enableDownload(recordEntity.getEnableDownload())
                .status(FileUploadTypeEnum.FILE_UPLOADED.getCode())
                .statusDesc(FileUploadTypeEnum.FILE_UPLOADED.getDesc())
                .fileName(recordEntity.getFileName())
                .originName(recordEntity.getOriginName())
                .enablePreview(recordEntity.getEnablePreview())
                .enableDownload(recordEntity.getEnableDownload())
                .enableEdit(recordEntity.getEnableEdit())
                .md5(recordEntity.getMd5())
                .size(recordEntity.getSize())
                .editKey(recordEntity.getEditKey())
                .insertTime(recordEntity.getCreateTime())
                .updateTime(recordEntity.getUpdateTime())
                .batch(recordEntity.getBatch())
                .build();
        return resultDTO;
    }






}
