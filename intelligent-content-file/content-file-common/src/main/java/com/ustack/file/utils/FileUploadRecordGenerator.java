package com.ustack.file.utils;


import com.ustack.file.entity.FileUploadRecordEntity;
import com.ustack.file.enums.FileUploadTypeEnum;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

/**
 * @author linxin
 * @Description : 生成数据库记录
 * @ClassName : FileUploadRecordGenerator
 * @Date: 2022-06-29 09:23
 */
@Builder
public class FileUploadRecordGenerator {

    private String uuid;

    private String fileOriginName;

    private String fileMd5;

    private String fullFileSaveDir;

    private String fileBasePath;

    private Long fileSize;

    private String documentId;

    public FileUploadRecordGenerator() {
    }

    public FileUploadRecordGenerator(String uuid, String fileOriginName, String fileMd5, String fullFileSaveDir, String fileBasePath, Long fileSize, String documentId) {
        this.uuid = uuid;
        this.fileOriginName = fileOriginName;
        this.fileMd5 = fileMd5;
        this.fullFileSaveDir = fullFileSaveDir;
        this.fileBasePath = fileBasePath;
        this.fileSize = fileSize;
        this.documentId = documentId;
    }

    public FileUploadRecordEntity generate(){
        FileUploadRecordEntity recordEntity = new FileUploadRecordEntity();
        String path = this.fullFileSaveDir.replace(fileBasePath, "");
        recordEntity.setOriginName(this.fileOriginName);
        String suffix = FileNameUtil.getSuffix(this.fileOriginName);
        recordEntity.setSuffix(suffix);
        recordEntity.setGuid(this.uuid);
        String fileName = StringUtils.isNotBlank(suffix) ? FileNameUtil.genFileName(this.uuid, suffix) : this.uuid;
        recordEntity.setFileName(fileName);
        recordEntity.setPath(path);
        recordEntity.setSize(FileUtil.getFileSize(this.fileSize));
        recordEntity.setMd5(this.fileMd5);
        recordEntity.setEnableDownload(true);
        recordEntity.setStatus(FileUploadTypeEnum.FILE_UPLOADED.getCode());
        //
        recordEntity.setOrderBy(0);
        recordEntity.setDocumentId(this.documentId);
        // 是否可编辑 office only在线编辑 根据文件类型判断
        if (FileUtil.FILE_EDITABLE.test(recordEntity.getSuffix())){
            recordEntity.setEnableEdit(true);
            // 生成onlyOffice editkey
            recordEntity.setEditKey(FileNameUtil.genEditKey());
        }
        if (FileUtil.FILE_CAN_PREVIEW.test(recordEntity.getSuffix())){
            recordEntity.setEnablePreview(true);
        }
        return recordEntity;
    }

}
