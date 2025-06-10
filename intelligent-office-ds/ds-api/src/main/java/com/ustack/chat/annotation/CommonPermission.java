package com.ustack.chat.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE}) // 注解作用于方法
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可用
public @interface CommonPermission {
}
