package com.ustack.resource.dto;

import lombok.Data;

/**
 * @author Liyingzheng
 * @data 2025/4/23 11:31
 * @describe
 */
@Data
public class UserTreeDTO {
    private String id;
    private String parentId;
    private String name;
    /**
     * 头像ID
     */
    private String avatar;
}
