package com.ustack.global.common.mybatis;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description: 注入mybatis plus真正的批量插入方法
 * @author：linxin
 * @ClassName: BatchInsertSqlInjector
 * @Date: 2025-03-14 14:02
 */
@Configuration
public class BatchInsertSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        // 注意：此SQL注入器继承了DefaultSqlInjector(默认注入器)，调用了DefaultSqlInjector的getMethodList方法，保留了mybatis-plus的自带方法
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        // 注入InsertBatchSomeColumn
        // 在!t.isLogicDelete()表示不要逻辑删除字段，!"update_time".equals(t.getColumn())表示不要字段名为 update_time 的字段
        methodList.add(new InsertBatchSomeColumn(t -> !t.isLogicDelete()
               // && !"update_time".equals(t.getColumn())  insert时修改时间字段，自动补充数据
//                && !"update_user".equals(t.getColumn())
//                && !"update_user_id".equals(t.getColumn())
        ));
        // 解决批量插入为字段为null 不写入默认值问题
//        methodList.add(new InsertBatchSomeColumn("insertBatchSomeColumn"));
        return methodList;
    }
}
