package com.thtf.op.service;

import com.thtf.global.common.rest.RestResponse;

/**
 * @author Liyingzheng
 * @data 2025/4/23 11:10
 * @describe
 */
public interface UserTreeService {
    /**
     * 选人接口
     *
     * @param searchStr 查询用户名称、部门
     * @param type      1全部，2本部门
     *                  type值为空或为全部时，返回同方本部、同方本部下的部门、同方本部下的用户数据
     *                  type值为本部门时，返回本部门下的用户数据
     * @param parentId  父节点id
     */
    RestResponse get(String searchStr, String type, String parentId);
}
