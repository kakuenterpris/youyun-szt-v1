package com.ustack.kbase.config;

import net.cnki.kbase.jdbc.KbaseDataSource;
import net.cnki.kbase.jdbc.template.KbaseTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * kbase数据源
 *
 * @author Yp
 */
@Configuration
public class KbaseConfig {

    @Value("${kbaseDataSource.ip}")
    private String ip;

    @Value("${kbaseDataSource.username}")
    private String username;

    @Value("${kbaseDataSource.password}")
    private String password;

    @Bean
    public KbaseDataSource kbaseDataSource() {
        return KbaseDataSource.create(ip, username, password);
    }

    @Bean(name = "kbaseTemplate")
    public KbaseTemplate getKbaseTemplate() {

        return new KbaseTemplate(kbaseDataSource());
    }

}
