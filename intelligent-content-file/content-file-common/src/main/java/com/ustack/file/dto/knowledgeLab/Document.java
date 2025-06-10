package com.ustack.file.dto.knowledgeLab;

import lombok.Data;

// 文档类
@Data
public class Document {
    private String id;
    private int position;
    private String data_source_type;
    private DataSourceInfo data_source_info;
    private String dataset_process_rule_id;
    private String name;
    private String created_from;
    private String created_by;
    private long created_at;
    private int tokens;
    private String indexing_status;
    private Object error;
    private boolean enabled;
    private Object disabled_at;
    private Object disabled_by;
    private boolean archived;
    private String display_status;
    private int word_count;
    private int hit_count;
    private String doc_form;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getData_source_type() {
        return data_source_type;
    }

    public void setData_source_type(String data_source_type) {
        this.data_source_type = data_source_type;
    }

    public DataSourceInfo getData_source_info() {
        return data_source_info;
    }

    public void setData_source_info(DataSourceInfo data_source_info) {
        this.data_source_info = data_source_info;
    }

    public String getDataset_process_rule_id() {
        return dataset_process_rule_id;
    }

    public void setDataset_process_rule_id(String dataset_process_rule_id) {
        this.dataset_process_rule_id = dataset_process_rule_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_from() {
        return created_from;
    }

    public void setCreated_from(String created_from) {
        this.created_from = created_from;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public String getIndexing_status() {
        return indexing_status;
    }

    public void setIndexing_status(String indexing_status) {
        this.indexing_status = indexing_status;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Object getDisabled_at() {
        return disabled_at;
    }

    public void setDisabled_at(Object disabled_at) {
        this.disabled_at = disabled_at;
    }

    public Object getDisabled_by() {
        return disabled_by;
    }

    public void setDisabled_by(Object disabled_by) {
        this.disabled_by = disabled_by;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getDisplay_status() {
        return display_status;
    }

    public void setDisplay_status(String display_status) {
        this.display_status = display_status;
    }

    public int getWord_count() {
        return word_count;
    }

    public void setWord_count(int word_count) {
        this.word_count = word_count;
    }

    public int getHit_count() {
        return hit_count;
    }

    public void setHit_count(int hit_count) {
        this.hit_count = hit_count;
    }

    public String getDoc_form() {
        return doc_form;
    }

    public void setDoc_form(String doc_form) {
        this.doc_form = doc_form;
    }
}
