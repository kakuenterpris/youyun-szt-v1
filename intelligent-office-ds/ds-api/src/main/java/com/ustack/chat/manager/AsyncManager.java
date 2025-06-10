package com.ustack.chat.manager;

import com.ustack.chat.utils.ThreadsUtils;
import com.ustack.chat.spring.SpringUtils;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AsyncManager {

    /**
     * 操作延迟10毫秒
     */
    private final int OPERATE_DELAY_TIME = 10;

    /**
     * 异步操作任务调度线程池
     */
    private ScheduledExecutorService executor = SpringUtils.getBean("scheduledExecutorService");

    /**
     * 单例模式
     */
//    private AsyncManager(){}
//
//    private static AsyncManager me = new AsyncManager();
//
//    public static AsyncManager me()
//    {
//        return me;
//    }


    private static volatile AsyncManager instance;

    // 私有构造函数避免外部实例化
    private AsyncManager() {
        // 移除构造函数中的 Spring 依赖
    }

    public static AsyncManager me() {
        if (instance == null) {
            synchronized (AsyncManager.class) {
                if (instance == null) {
                    instance = new AsyncManager();
                }
            }
        }
        return instance;
    }



    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void execute(TimerTask task)
    {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止任务线程池
     */
    public void shutdown()
    {
        ThreadsUtils.shutdownAndAwaitTermination(executor);
    }
}
