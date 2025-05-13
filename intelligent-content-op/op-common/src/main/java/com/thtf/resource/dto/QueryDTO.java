package com.thtf.resource.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 查询 DTO
 * @author allm
 */
@Data
public class QueryDTO {

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 文件名
     */
    private String name;

    /**
     * id
     */
    private List<Integer> idList;

    /**
     * 父级id
     */
    private Integer parentId;

    /**
     * 文件年份
     */
    private List<Integer> fileYearList;

    /**
     * 是否参与问答
     */
    private Boolean joinQuery;

    /**
     * 向量化配置
     */
    private List<String> embeddingConfigNameList;

    /**
     * 可查看的部门
     */
    private List<String> authDepNumList;

    /**
     * 创建时间
     */
    private String nameSort;

    /**
     * 更新时间
     */
    private String timeSort;
}
