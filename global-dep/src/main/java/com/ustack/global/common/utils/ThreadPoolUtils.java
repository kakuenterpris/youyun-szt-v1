package com.ustack.global.common.utils;


import cn.hutool.core.lang.Assert;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 线程池工具类，使用alibaba ttl包装线程池，解决异步方法中ThreadLocal不传递问题
 *
 * @author linxin
 * @param: null
 * @date 2024/1/25 11:28
 */
public class ThreadPoolUtils {

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 拒绝策略 caller run
     *
     * @return DtpExecutor
     * @param: poolName
     * @param: keepAliveTime
     * @param: timeUnit
     * @param: queueSize
     * @author linxin
     * @date 2024/1/25 13:23
     */
    public static ExecutorService getExecutorCallerRunPolicy(String poolName, int keepAliveTime, TimeUnit timeUnit, int queueSize) {
        return getDefaultSizeExecutor(poolName, keepAliveTime, timeUnit, new ThreadPoolExecutor.CallerRunsPolicy(), queueSize);
    }


    /**
     * 拒绝策略 Abort
     *
     * @return DtpExecutor
     * @param: poolName
     * @param: keepAliveTime
     * @param: timeUnit
     * @param: queueSize
     * @author linxin
     * @date 2024/1/25 13:58
     */
    public static ExecutorService getExecutorAbortPolicy(String poolName, Integer keepAliveTime, TimeUnit timeUnit, Integer queueSize) {
        return getDefaultSizeExecutor(poolName, keepAliveTime, timeUnit, new ThreadPoolExecutor.AbortPolicy(), queueSize);
    }

    /**
     * 默认大小线程池
     *
     * @return DtpExecutor
     * @param: poolName
     * @param: keepAliveTime
     * @param: timeUnit
     * @param: rejectPolicy
     * @param: queueSize
     * @author linxin
     * @date 2024/1/25 13:58
     */
    public static ExecutorService getDefaultSizeExecutor(String poolName, Integer keepAliveTime, TimeUnit timeUnit, RejectedExecutionHandler rejectPolicy, Integer queueSize) {
        return getExecutor(poolName, CORE_SIZE, MAX_SIZE, keepAliveTime, timeUnit, rejectPolicy, queueSize);
    }


    /**
     * 阿里 ttl 包装
     *
     * @return ExecutorService
     * @param: poolName
     * @param: coreSize
     * @param: maxSize
     * @param: keepAliveTime
     * @param: timeUnit
     * @param: rejectPolicy
     * @param: queueSize
     * @author linxin
     * @date 2024/1/25 15:26
     */
    public static ExecutorService getExecutor(String poolName, Integer corePoolSize, Integer maximumPoolSize, Integer keepAliveTime, TimeUnit timeUnit, RejectedExecutionHandler rejectPolicy, Integer queueSize) {

        Assert.notBlank(poolName, () -> new IllegalArgumentException("poolName is blank!"));
        Assert.isTrue(Objects.nonNull(keepAliveTime) && keepAliveTime > 0, () -> new IllegalArgumentException("keepAliveTime: " + Objects.toString(keepAliveTime, "null") + " is illegal!"));
        Assert.isTrue(Objects.nonNull(corePoolSize) && corePoolSize > 0 && corePoolSize <= CORE_SIZE, () -> new IllegalArgumentException("coreSize: " + Objects.toString(corePoolSize, "null") + " is illegal!"));
        Assert.isTrue(Objects.nonNull(maximumPoolSize) && maximumPoolSize > 0 && maximumPoolSize < (8 * MAX_SIZE), () -> new IllegalArgumentException("maxSize: " + Objects.toString(maximumPoolSize, "null") + " is illegal!"));
        Assert.isTrue(Objects.nonNull(queueSize) && queueSize > 0 && queueSize < 100000, () -> new IllegalArgumentException("queueSize: " + Objects.toString(queueSize, "null") + " is illegal!"));
        Assert.notNull(timeUnit, () -> new IllegalArgumentException("timeUnit is NULL!"));
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                timeUnit,
                new ArrayBlockingQueue<>(queueSize),
                r -> new Thread(r, poolName),
                rejectPolicy
        );
        return TtlExecutors.getTtlExecutorService(threadPool);
    }
}