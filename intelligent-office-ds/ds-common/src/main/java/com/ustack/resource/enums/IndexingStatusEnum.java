package com.ustack.resource.enums;

/**
 * 知识化状态-枚举
 */
public enum IndexingStatusEnum {

    // 枚举项
    WAITING("waiting", "排队中"),
    PARSING("parsing", "解析中"),
    CLEANING("cleaning", "清洗中"),
    SPLITTING("splitting", "拆分中"),
    INDEXING("indexing", "向量中"),
    PAUSED("paused", "暂停"),
    ERROR("error", "异常"),
    COMPLETED("completed", "完成");

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
