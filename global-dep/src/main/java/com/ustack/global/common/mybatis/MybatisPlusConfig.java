package com.ustack.global.common.mybatis;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ustack.global.common.rest.ContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author linxin
 * @Description : 分页乐观锁和自动填充字段
 * @ClassName : MybatisPlusConfig
 * @Date: 2022-10-31 08:55
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class MybatisPlusConfig implements MetaObjectHandler {

    private final static String CREATE_TIME = "createTime";
    private final static String CREATE_USERID = "createUserId";
    private final static String CREATE_USER = "createUser";
    private final static String VERSION = "version";
    private final static String GUID = "guid";
    private final static String UPDATE_TIME = "updateTime";
    private final static String UPDATE_USERID = "updateUserId";
    private final static String UPDATE_USER = "updateUser";

    public final static Date DEFAULT_UPDATE_TIME;

    static {
        try {
            DEFAULT_UPDATE_TIME = DateUtils.parseDate("1900-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    private static Predicate<Object> hasField = (v) -> Objects.nonNull(v) && StringUtils.isNotBlank(v.toString());

    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建人信息字段,如果参数有值则使用参数自带的值
        Object guidValue = metaObject.getValue(GUID);
        String guid = hasField.test(guidValue) ? guidValue.toString() : IdUtil.simpleUUID();
        Object createUserValue = metaObject.getValue(CREATE_USER);
        String userName = hasField.test(createUserValue) ? createUserValue.toString() : ContextUtil.getUserName();
        Object createUserIdValue = metaObject.getValue(CREATE_USERID);
        String userId = hasField.test(createUserIdValue) ? createUserIdValue.toString() : ContextUtil.getUserId();

        metaObject.setValue(CREATE_TIME, null);
        metaObject.setValue(CREATE_USER, null);
        metaObject.setValue(CREATE_USERID, null);
        metaObject.setValue(UPDATE_TIME, null);
        metaObject.setValue(UPDATE_USER, null);
        metaObject.setValue(UPDATE_USERID, null);
        metaObject.setValue(VERSION, null);
        metaObject.setValue(GUID, null);
        Date currentDate = new Date();

        this.strictInsertFill(metaObject, "updateTime",() -> currentDate,  Date.class);
        this.strictInsertFill(metaObject, "updateUser",() -> userName, String.class);
        this.strictInsertFill(metaObject, "updateUserId",() -> userId, String.class);
        this.strictInsertFill(metaObject, "createTime",() -> currentDate,  Date.class);
        this.strictInsertFill(metaObject, "createUserId", () -> userId, String.class);
        this.strictInsertFill(metaObject, "createUser", () -> userName, String.class);
        // 乐观锁字段
        this.strictInsertFill(metaObject, "version", () -> 1L, Long.class);
        this.strictInsertFill(metaObject, "guid", () -> guid, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue(UPDATE_TIME, null);
        metaObject.setValue(UPDATE_USER, null);
        metaObject.setValue(UPDATE_USERID, null);
        this.strictUpdateFill(metaObject, "updateTime", () -> new Date(),  Date.class);
        this.strictUpdateFill(metaObject, "updateUserId",  () -> ContextUtil.getUserId(), String.class);
        this.strictUpdateFill(metaObject, "updateUser", () -> ContextUtil.getUserName(), String.class);
    }
}
