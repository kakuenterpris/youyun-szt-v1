package com.ustack.file.dto.onlyoffice;

import lombok.Data;

import java.util.HashMap;

/**
 * @author linxin
 * @Description : word文档执行邮件合并参数
 * @ClassName : MailMergeDTO
 * @Date: 2021-10-18 13:34
 */
@Data
public class MailMergeDTO {

    /**
     * 模板guid
     */
    private String templateGuid;
    /**
     * 普通合并数据
     */
    private String[] data;
    /**
     * 区域合并数据（表格）
     */
    private HashMap<String, String[][]> regions;
    /**
     * 保存名称（不传默认是当前模板文件路径 模板名称加随机字符）
     */
    private String saveName;
}
