package com.ustack.global.common.serialize;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Description: 序列化反序列化配置
 * @author：linxin
 * @ClassName: JacksonConfiguration
 * @Date: 2025-02-17 09:56
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //设置日期格式
        ExtendObjectMapper objectMapper = new ExtendObjectMapper();
        SimpleDateFormat smt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        objectMapper.setDateFormat(smt);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(0, mappingJackson2HttpMessageConverter);
        return mappingJackson2HttpMessageConverter;
    }
}
