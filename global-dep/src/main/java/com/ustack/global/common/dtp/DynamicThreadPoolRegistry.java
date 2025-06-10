package com.ustack.global.common.dtp;

import com.ustack.global.common.application.listener.ThreadPoolExecutorShutdownListener;

import java.util.concurrent.ExecutorService;

/**
 * @Description: 注册动态线程池和 Spring 优雅关闭线程池
 * @author：linxin
 * @ClassName: DynamicThreadPoolRegistry
 * @Date: 2024-01-25 13:46
 */
public class DynamicThreadPoolRegistry {

    /**
     * 默认动态线程池
     */
    private final ThreadPoolExecutorShutdownListener shutdownListener;

    public DynamicThreadPoolRegistry(ThreadPoolExecutorShutdownListener shutdownListener) {
        this.shutdownListener = shutdownListener;
    }

    /**
     * 注册 线程池 被Spring管理，优雅关闭
     * @param: executorService
     * @author linxin
     * @return void
     * @date 2024/1/25 15:22
     */
    public void register(ExecutorService executorService) {
        shutdownListener.registryExecutor(executorService);
    }

}
