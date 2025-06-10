package com.ustack.file.dto.knowledgeLab;

import lombok.Data;

// 数据源信息类
@Data
public class DataSourceInfo {
    private String upload_file_id;

    public String getUpload_file_id() {
        return upload_file_id;
    }

    public void setUpload_file_id(String upload_file_id) {
        this.upload_file_id = upload_file_id;
    }
}