package com.jdxiaokang.core.pool;

import java.util.concurrent.ExecutorService;

import static com.jdxiaokang.core.pool.ThreadPoolFactory.*;


/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 线程池枚举
 * @date 2020/2/5
 */
public enum  ThreadPoolSingleton {
    /**
     * 业务请求线程池
     */
    SERVICE_EXECUTOR(createServiceExecutor("service-exec-")),
    /**
     * 业务中异步任务线程池
     */
    IDLE_TASK_EXECUTOR(createIdleTaskExecutor("idle-task-exec-")),
    /**
     * 大任务分配子任务的线程池，大任务需要等待所有子任务完成
     */
    DISPATCH_TASK_EXECUTOR(createIdleTaskExecutor("dispatch-task-exec-"));

    ThreadPoolSingleton(ExecutorService executor) {
        this.executor = executor;
    }

    private ExecutorService executor;

    public ExecutorService getExecutor() {
        return executor;
    }
}
