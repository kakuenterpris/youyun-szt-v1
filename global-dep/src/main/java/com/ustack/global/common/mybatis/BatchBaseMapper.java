package com.ustack.global.common.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: 批量插入
 * @author：linxin
 * @ClassName: BatchBaseMapper
 * @Date: 2025-03-14 14:07
 */
public interface BatchBaseMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入
     * {@link com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn}
     * @param entityList 要插入的数据
     * @return 成功插入的数据条数
     */
    int insertBatchSomeColumn(List<T> entityList);

}
