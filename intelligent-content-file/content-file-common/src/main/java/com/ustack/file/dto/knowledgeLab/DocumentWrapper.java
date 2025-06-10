package com.ustack.file.dto.knowledgeLab;


import lombok.Data;
/**
 * 文档生成知识库有云返回对象
 */
@Data
public class DocumentWrapper {

    private Document document;

    private String batch;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}