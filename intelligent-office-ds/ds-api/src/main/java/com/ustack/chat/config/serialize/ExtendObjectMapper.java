package com.ustack.chat.config.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class ExtendObjectMapper extends ObjectMapper {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";

    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    public ExtendObjectMapper() {
        super();
        // 收到未知属性时不报异常
        this.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 统一返回数据的输出风格 转为蛇形命名法
        this.setPropertyNamingStrategy(new PropertyNamingStrategies.LowerCamelCaseStrategy());
        // 反序列化时，属性不存在的兼容处理
        this.getDeserializationConfig()
                .withoutFeatures(FAIL_ON_UNKNOWN_PROPERTIES);

        // 格式化时间
        JavaTimeModule module = new JavaTimeModule();

        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                .addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)))
                .addDeserializer(Date.class, new DateDeserializers.DateDeserializer())

//                 .addSerializer(BigInteger.class, ToStringSerializer.instance)
                .addSerializer(Long.class, new NumberSerializers.LongSerializer(Long.class))
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                .addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)))
                .addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT)))
        ;
        // 注册功能模块 添加自定义序列化器和反序列化器
        this.registerModule(module);
    }
}
