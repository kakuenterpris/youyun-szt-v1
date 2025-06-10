package com.ustack.feign.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ustack.feign.client.KbaseApi;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.kbase.entity.CompanyVector;
import com.ustack.kbase.entity.DepartmentVector;
import com.ustack.kbase.entity.PersonalVector;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;

/**
 * @author zhangwei
 * @date 2025年03月29日
 */
@Configuration
@EnableFeignClients(basePackages = "com.ustack.feign.client")
@Slf4j
public class KbaseFeignAutoconfiguration {
    @Bean
    public FeignClientsConfiguration customFeignClientConfiguration() {
        return new FeignClientsConfiguration();
    }

    @Bean
    public Decoder feignDecoder() {
        return new CustomFeignDecoder();
    }


    public class CustomFeignDecoder implements Decoder {

        private final ObjectMapper objectMapper = new ObjectMapper();
        private final HttpMessageConverter<Object> messageConverter = new MappingJackson2HttpMessageConverter(objectMapper);

        @Override
        public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
            try {
                if (response.status() == HttpStatus.NO_CONTENT.value() || response.body() == null) {
                    return null;
                }
                byte[] responseBytes = response.body().asInputStream().readAllBytes();
                String jsonString = new String(responseBytes, StandardCharsets.UTF_8);
                log.info("Feign Response => Status: {}, Headers: {}, Body: {}",
                        response.status(),
                        response.headers(),
                        jsonString);
                // 获取Content-Type
                Collection<String> contentTypeHeader = response.headers().get("Content-Type");
                MediaType mediaType = contentTypeHeader != null && !contentTypeHeader.isEmpty()
                        ? MediaType.parseMediaType(contentTypeHeader.iterator().next())
                        : MediaType.APPLICATION_JSON;  // 默认使用JSON

                // 使用已读取的内容
                return messageConverter.read((Class<?>) type,
                        new FakeHttpInputMessage(jsonString, mediaType));
            } catch (Exception e) {
                log.error("Decode failed! Response: {}", response, e);
                throw e;
            }
        }
//            if (response.status() == HttpStatus.NO_CONTENT.value() || response.body() == null) {
//                return null;
//            }
//            Collection<String> contentTypeHeader = response.headers().getOrDefault("Content-Type",
//                    Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM_VALUE));
//            log.info("Response headers:{}", response.headers());
//            String contentType;
//            if (contentTypeHeader != null && !contentTypeHeader.isEmpty()) {
//                contentType = contentTypeHeader.iterator().next();
//            } else {
//                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
//            }
//            MediaType mediaType = MediaType.parseMediaType(contentType);
//            String body = StreamUtils.copyToString(response.body().asInputStream(), StandardCharsets.UTF_8);
//            return messageConverter.read((Class<?>) type, new FakeHttpInputMessage(body, mediaType));
//        }
    }

    public static class FakeHttpInputMessage implements org.springframework.http.HttpInputMessage {
        private final String body;
        private final MediaType mediaType;

        public FakeHttpInputMessage(String body, MediaType mediaType) {
            this.body = body;
            this.mediaType = mediaType;
        }

        @Override
        public InputStream getBody() throws IOException {
            return new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(mediaType);
            return headers;
        }
    }
}
