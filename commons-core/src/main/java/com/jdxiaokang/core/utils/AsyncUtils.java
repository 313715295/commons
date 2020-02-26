package com.jdxiaokang.core.utils;

import com.jdxiaokang.commons.exceptions.ServiceException;
import com.jdxiaokang.commons.support.LogAction;
import com.jdxiaokang.commons.support.VoidAction;
import com.jdxiaokang.core.pool.ThreadPoolSingleton;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.jdxiaokang.commons.utils.ThrowableUtils.getCallErrorLogWithCall;
import static com.jdxiaokang.core.pool.ThreadPoolSingleton.IDLE_TASK_EXECUTOR;


/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 异步任务公共方法
 * @date 2019/12/31
 */
@Slf4j
public class AsyncUtils {
    //默认线程池，tomcat的处理方式
    private static final ExecutorService DEFAULT_EXECUTOR = ThreadPoolSingleton.SERVICE_EXECUTOR.getExecutor();

    /**
     * 捕获异步任务的异常，future类型在get的时候会返回异常，Runnable类型也会被包装成FutureTask，但不会只get步骤，异常会被吃掉
     * @param voidAction 执行逻辑
     * @param logAction 打印错误日志逻辑
     */
    public static void catchAsyncException(VoidAction voidAction, LogAction logAction) {
        try {
            voidAction.action();
        } catch (Exception e) {
           logAction.log(e);
        }
    }

    /**
     * 捕获异步任务的异常，future类型在get的时候会返回异常，Runnable类型也会被包装成FutureTask，但不会只get步骤，异常会被吃掉
     * @param voidAction 执行逻辑
     * @param taskName 任务名称
     */
    public static void catchAsyncException(VoidAction voidAction,String taskName) {
        try {
            voidAction.action();
        } catch (Exception e) {
            log.error("异步任务=[{}] 执行过程中发生异常:[{}]", taskName, getCallErrorLogWithCall(e));
        }
    }
    /**
     * 处理空闲任务，最终一致性，以后会丢消息队列处理
     * @param voidAction            执行逻辑
     */
    public static  void idleTask(@Nonnull VoidAction voidAction) {
        IDLE_TASK_EXECUTOR.getExecutor().submit(voidAction::action);
    }
    /**
     * 处理空闲任务，最终一致性，以后会丢消息队列处理
     * @param voidAction            执行逻辑
     * @param logAction 打印错误日志逻辑
     */
    public static  void idleTask(@Nonnull VoidAction voidAction,LogAction logAction) {
        idleTask(IDLE_TASK_EXECUTOR.getExecutor(),voidAction,logAction);
    }
    /**
     * 处理空闲任务，最终一致性，以后会丢消息队列处理
     * @param executorService 指定线程池
     * @param voidAction            执行逻辑
     * @param logAction 打印错误日志逻辑
     */
    public static  void idleTask(ExecutorService executorService,@Nonnull VoidAction voidAction,@Nonnull LogAction logAction) {
        Optional.ofNullable(executorService).orElse(IDLE_TASK_EXECUTOR.getExecutor())
                .submit(() -> catchAsyncException(voidAction, logAction));
    }
    /**
     * 处理空闲任务，最终一致性，以后会丢消息队列处理
     * @param voidAction            执行逻辑
     * @param taskName 任务名称
     */
    public static  void idleTask(@Nonnull VoidAction voidAction, String taskName) {
        IDLE_TASK_EXECUTOR.getExecutor().submit(() -> catchAsyncException(voidAction, taskName));
    }
    /**
     * 异步任务
     * @param task            任务
     * @return 结果Future
     */
    public static  <E> Future<E> asyncTask(@Nonnull Callable<E> task) {
        return DEFAULT_EXECUTOR.submit(task);
    }
    /**
     * 异步任务
     * @param executorService 线程池
     * @param task            任务
     * @return 结果Future
     */
    public static  <E> Future<E> asyncTask(ExecutorService executorService, @Nonnull Callable<E> task) {
        return Optional.ofNullable(executorService).orElse(DEFAULT_EXECUTOR).submit(task);
    }
    /**
     * 获取异步任务的结果
     * @param future 异步任务Future
     * @param <E> 返回类型
     * @return 结果
     */
    public static  <E> E getAsyncResult(@Nonnull Future<E> future) {
        return getAsyncResult(future, "获取数据失败");
    }

    /**
     * 获取异步任务的结果
     * @param future 异步任务Future
     * @param errorMsg 错误消息提示
     * @param <E> 返回类型
     * @return 结果
     */
    public static  <E> E getAsyncResult(@Nonnull Future<E> future, String errorMsg) {
        return Optional.of(future).map(task -> {
            try {
                return task.get();
            } catch (InterruptedException e) {
                log.error("异步调用方法被中断:[{}]", getCallErrorLogWithCall(e));
                throw new ServiceException(errorMsg,e);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ServiceException) {
                    log.error("异步获取结果异常:[{}]", getCallErrorLogWithCall(cause));
                    throw (ServiceException) cause;
                } else {
                    log.error("异步获取结果异常:[{}]", getCallErrorLogWithCall(cause));
                    throw new ServiceException(errorMsg,cause);
                }
            }
            //获取数据为null的情况由调用者处理
        }).orElse(null);
    }





}
