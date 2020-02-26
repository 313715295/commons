package com.jdxiaokang.core.pool;

import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jdxiaokang.core.pool.ThreadConfig.WEB_MIN_THREADS;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 线程池统一创建方法
 * @date 2020/2/5
 */
public class ThreadPoolFactory {


    /**
     * 处理空闲任务的线程池，不需要等待请求结果，紧急性要求不高，队列处理即可
     * 线程空闲5分钟关闭，包括核心线程  默认线程数cpu*5,后续再拓展自定义参数
     */
    public static ExecutorService createIdleTaskExecutor(String namePrefix) {
        //用最小的线程数处理即可，无界队列    线程空闲5分钟关闭
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(WEB_MIN_THREADS,
                WEB_MIN_THREADS, 300L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            AtomicInteger flag = new AtomicInteger(0);
            @Override
            public Thread newThread(@Nonnull Runnable runnable) {
                Thread result = new Thread(runnable, namePrefix+flag.getAndIncrement());
                result.setDaemon(false);
                return result;
            }
        });
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
    /**
     * 处理业务请求中的线程池，需要等待请求结果的。有请求的时候优先新建线程处理，直到最大线程数，然后再丢入队列
     * 空闲线程5分钟关闭  队列参考tomcat工作队列
     */
    public static ExecutorService createServiceExecutor(String namePrefix) {
        //用的tomcat的线程池模式，工作队列
        TaskQueue taskqueue = new TaskQueue();
        TaskThreadFactory tf = new TaskThreadFactory(namePrefix, false,5);
        org.apache.tomcat.util.threads.ThreadPoolExecutor threadPoolExecutor = new org.apache.tomcat.util.threads.ThreadPoolExecutor(WEB_MIN_THREADS,
                ThreadConfig.WEB_MAX_THREAD, 60L, TimeUnit.SECONDS, taskqueue, tf);
        taskqueue.setParent(threadPoolExecutor);
        return threadPoolExecutor;
    }
}
