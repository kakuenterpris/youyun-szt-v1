package com.ustack.op.config;


import com.ustack.global.common.application.listener.ThreadPoolExecutorShutdownListener;
import com.ustack.global.common.dtp.DynamicThreadPoolRegistry;
import com.ustack.global.common.utils.ThreadPoolUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: ThreadPoolConfig
 * @Date: 2024-01-25 10:50
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ThreadPoolConfig {

    private final static Integer MIN_QUEUE_SIZE = 100;
    private final static Integer QUEUE_SIZE = 10000;
    private final static TimeUnit TIME_UNIT = TimeUnit.MINUTES;


    @Bean
    public ThreadPoolExecutorShutdownListener executorShutdownListener(){
        return new ThreadPoolExecutorShutdownListener();
    }

    @Bean("queryExecutorService")
    public ExecutorService queryExecutorService(){
        ExecutorService executor = ThreadPoolUtils.getExecutorCallerRunPolicy("query-common-pool", 30, TIME_UNIT, QUEUE_SIZE);
        return executor;
    }

    @Bean("logExecutorService")
    public ExecutorService logExecutorService(){
        ExecutorService executor = ThreadPoolUtils.getExecutorCallerRunPolicy("log-common-pool", 30, TIME_UNIT, QUEUE_SIZE);
        return executor;
    }

    @Bean("statisticExecutorService")
    public ExecutorService statisticExecutorService(){
        ExecutorService executor = ThreadPoolUtils.getExecutorCallerRunPolicy("stat-common-pool", 30, TIME_UNIT, QUEUE_SIZE);
        return executor;
    }

    @Bean("dynamicThreadPoolRegistry")
    public DynamicThreadPoolRegistry dynamicThreadPoolRegistry(){
        DynamicThreadPoolRegistry poolRegistry = new DynamicThreadPoolRegistry(executorShutdownListener());
        // 注册到spring 管理
        poolRegistry.register(queryExecutorService());
        poolRegistry.register(logExecutorService());
        poolRegistry.register(statisticExecutorService());
        return poolRegistry;
    }





}
