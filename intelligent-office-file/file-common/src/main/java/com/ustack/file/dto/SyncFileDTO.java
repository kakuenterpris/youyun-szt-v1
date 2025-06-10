package com.ustack.file.dto;

import com.ustack.file.dto.knowledgeLab.SyncFileDataDTO;
import com.ustack.resource.dto.FileEmbeddingConfigDTO;
import lombok.Data;

import java.util.List;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: SyncFileDTO
 * @Date: 2025-02-20 14:43
 */
@Data
public class SyncFileDTO {

    /**
     * 知识库id
     */
    private String datasetId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件向量化配置编码
     */
    private String fileEmbeddingConfigCode;

    /**
     * 文件向量化配置
     */
    private String fileEmbeddingConfigName;

    /**
     * 文件向量化配置
     */
    private SyncFileDataDTO config;

    /**
     * 文件向量化配置
     */
    private FileEmbeddingConfigDTO embeddingConfig;

}
