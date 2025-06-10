package com.ustack.file.dto.onlyoffice;


import lombok.Data;

/**
 * @author linxin
 * only office保存回调参数
 */
@Data
public class EditCallbackDto {

    private EditCallbackActionsDto[] actions;

    private String changesurl;

    private int forcesavetype;

    private EditCallbackHistoryDto history;

    private String key;

    private int status;

    private String url;

    private String userdata;

    private String[] users;

}
