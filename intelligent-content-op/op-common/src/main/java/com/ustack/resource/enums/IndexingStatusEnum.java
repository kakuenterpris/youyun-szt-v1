package com.ustack.resource.enums;

/**
 * 知识化状态-枚举
 */
public enum IndexingStatusEnum {

    // 枚举项
    WAITING("waiting", "排队中"),
    PARSING("4", "提取中"),
    CLEANING("cleaning", "清洗中"),
    SPLITTING("splitting", "拆分中"),
    INDEXING("indexing", "向量中"),
    PAUSED("paused", "暂停"),
    ERROR("error", "异常"),
    UPLOAD_RAG_FAIL("uploadRagFail", "上传知识库失败"),
    FILE_ERROR("fileError", "文件为空异常"),
    PARSE_ERROR("3", "提取失败"),
    RAG_CONFIG_ERROR("ragRagError", "知识库配置异常"),
    RAG_ERROR("ragError", "知识库异常"),
    CHUNKS_ERROR("chunksError", "解析异常"),
    CHUNKS_EMPTY("chunksEmpty", "解析为空"),
    EMBEDDING_ERROR("embeddingError", "向量化异常"),
    EMBEDDING_LENGTH_ERROR("embeddingLengthError", "向量化长度异常"),
    EMBEDDING_SAVE_ERROR("embeddingSaveError", "向量化存储异常"),
    DELETE_CHUNKS_ERROR("deleteChunksError", "删除切片文档失败"),
    DELETE_EMBEDDING_ERROR("deleteEmbeddingError", "删除向量化数据失败"),
    SYNC_JOIN_QUERY_ERROR("joinQueryError", "现行有效状态同步失败"),
    RAG_CREATE_ERROR("ragCreateError", "创建知识库失败"),
    COMPLETED("2", "提取成功");

    // 枚举字段
    private final String indexingStatus;
    private final String indexingStatusName;

    // 构造方法
    IndexingStatusEnum(String indexingStatus, String indexingStatusName) {
        this.indexingStatus = indexingStatus;
        this.indexingStatusName = indexingStatusName;
    }

    // Getter 方法
    public String getIndexingStatus() {
        return indexingStatus;
    }

    public String getIndexingStatusName() {
        return indexingStatusName;
    }

    /**
     * 根据 indexingStatus 查找枚举项
     * @param indexingStatus
     * @return
     */
    public static IndexingStatusEnum fromIndexingStatus(String indexingStatus) {
        for (IndexingStatusEnum enumItem : values()) {
            if (enumItem.getIndexingStatus().equals(indexingStatus)) {
                return enumItem;
            }
        }
        return null;
    }
}
