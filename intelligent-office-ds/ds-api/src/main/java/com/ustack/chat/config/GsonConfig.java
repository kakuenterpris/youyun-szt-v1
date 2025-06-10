package com.ustack.chat.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

@Configuration
public class GsonConfig {
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        // 增强过滤条件
                        return Throwable.class.isAssignableFrom(f.getDeclaringClass())
                                && "detailMessage".equals(f.getName());
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(Charset.class, new CharsetTypeAdapter())
                .create();
    }
}

