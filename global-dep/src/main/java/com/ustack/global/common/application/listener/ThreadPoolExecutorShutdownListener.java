package com.ustack.global.common.application.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 监听容器关闭，优雅关闭线程池
 * @author：linxin
 * @ClassName: ThreadPoolExecutorShutdownListener
 * @Date: 2024-01-25 11:13
 */
public class ThreadPoolExecutorShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private final static Logger log = LoggerFactory.getLogger(ThreadPoolExecutorShutdownListener.class);

    /**
     * 保存需要优雅关闭的线程池
     */
    private final List<ExecutorService> THREAD_POOLS = Collections.synchronizedList(new ArrayList<>(12));

    /**
     * 线程中的任务在接收到应用关闭信号量后最多等待多久就强制终止，给剩余任务预留的时间，到时间后线程池必须销毁
     */
    private final long AWAIT_TERMINATION = 20;

    /**
     * awaitTermination的单位
     */
    private final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public void registryExecutor(ExecutorService executor) {
        THREAD_POOLS.add(executor);
    }

    /**
     * 参考{@link org.springframework.scheduling.concurrent.ExecutorConfigurationSupport#shutdown()}
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("容器关闭前处理线程池优雅关闭开始, 当前要处理的线程池数量为: {} >>>>>>>>>>>>>>>>", THREAD_POOLS.size());
        if (CollectionUtils.isEmpty(THREAD_POOLS)) {
            return;
        }
        for (ExecutorService pool : THREAD_POOLS) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(AWAIT_TERMINATION, TIME_UNIT)) {
                    if (log.isWarnEnabled()) {
                        log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                    }
                }
            } catch (InterruptedException ex) {
                if (log.isWarnEnabled()) {
                    log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                }
                Thread.currentThread().interrupt();
            }
        }
    }

}
