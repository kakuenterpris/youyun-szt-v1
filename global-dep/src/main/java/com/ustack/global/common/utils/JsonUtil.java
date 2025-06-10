package com.ustack.global.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: JsonUtil
 * @Date: 2025-02-17 10:29
 */
public class JsonUtil {

    private static final Gson gson;

    static {
        // 创建 Gson 实例，可根据需要进行配置，如设置日期格式等
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }

    /**
     * 将 Java 对象转换为 JSON 字符串
     * @param object 要转换的 Java 对象
     * @return JSON 字符串
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * 将 JSON 字符串转换为指定类型的 Java 对象
     * @param json JSON 字符串
     * @param clazz 目标 Java 对象的类
     * @param <T> 泛型类型
     * @return 转换后的 Java 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * 将 JSON 字符串转换为指定类型的 Java 对象列表
     * @param json JSON 字符串
     * @param <T> 泛型类型
     * @return 转换后的 Java 对象列表
     */
    public static <T> List<T> fromJsonToList(String json) {
        Type type = new TypeToken<List<T>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
