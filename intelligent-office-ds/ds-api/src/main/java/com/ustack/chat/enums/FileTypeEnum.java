package com.ustack.chat.enums;

/**
 * @author zhangwei
 * @date 2025年02月21日
 */
public enum FileTypeEnum {
    document("document","TXT, MARKDOWN, MDX, PDF, HTML, XLSX, XLS, DOCX, CSV, MD, HTM,txt,markdown,mdx,pdf,html,xlsx,xls,docx,csv,md,htm");

    private String type;
    private String content;

    FileTypeEnum(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
