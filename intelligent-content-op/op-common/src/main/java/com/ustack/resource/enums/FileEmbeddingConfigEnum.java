package com.ustack.resource.enums;

public enum FileEmbeddingConfigEnum {

    GENERAL_CONFIG ("general_config", "默认"),
    NOTICE ("notice", "通知"),
    NEWSLETTER ("newsletter", "新闻稿"),
    SPEECH_SCRIPT ("speech_script", "发言稿"),
    RESEARCH_REPORT ("research_report", "研究报告"),
    MEETING_MINUTES ("meeting_minutes", "会议纪要"),
    PUBLIC_ACCOUNT ("public_account", "公众号"),
    DEP_RULE ("dep_rule", "部门规章制度");

    private final String code;
    private final String name;

    FileEmbeddingConfigEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据 configCode 查找枚举项
     * @param configCode
     * @return
     */
    public static FileEmbeddingConfigEnum fromCode(String configCode) {
        for (FileEmbeddingConfigEnum enumItem : values()) {
            if (enumItem.getCode().equals(configCode)) {
                return enumItem;
            }
        }
        return null;
    }
}
