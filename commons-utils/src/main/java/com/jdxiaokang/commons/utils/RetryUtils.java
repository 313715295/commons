package com.jdxiaokang.commons.utils;

import com.jdxiaokang.commons.exceptions.OptimisticLockException;
import com.jdxiaokang.commons.exceptions.RetryException;
import com.jdxiaokang.commons.exceptions.ServiceException;
import com.jdxiaokang.commons.support.VoidAction;

import java.util.function.Function;
import java.util.function.Supplier;


/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 重试的工具类
 * @date 2020/1/4
 */
public class RetryUtils {

    /**
     * 重试次数，例如乐观锁
     */
    public static int RETRY_NUM = 3;

    /**
     * 乐观锁操作重试，重试失败后抛出异常
     * @param retryAction 操作
     */
    public static void retry(VoidAction retryAction){
        int i = RETRY_NUM;
        while (true) {
            try {
                retryAction.action();
                break;
            } catch (RetryException e) {
                i--;
                if (i < 0) {
                    throw new ServiceException(e.getMessage());
                }
            }
        }
    }

    /**
     * 乐观锁操作重试，重试失败后抛出异常
     * @param retryAction 操作
     * @return 结果
     */
    public static<T> T retry(Supplier<T> retryAction) {
        int i = RETRY_NUM;
        while (true) {
            try {
                return retryAction.get();
            } catch (RetryException e) {
                i--;
                if (i < 0) {
                    throw new ServiceException(e.getMessage());
                }
            }
        }
    }
    /**
     * 乐观锁操作重试，重试失败后抛出指定异常
     * @param retryAction 操作
     * @param exceptionFunction 指定异常生成操作
     */
    public static<X extends Throwable> void retry(VoidAction retryAction, Function<String,X> exceptionFunction) throws X {
        int i = RETRY_NUM;
        while (true) {
            try {
                retryAction.action();
                break;
            } catch (OptimisticLockException e) {
                i--;
                if (i < 0) {
                    throw exceptionFunction.apply(e.getMessage());
                }
            }
        }
    }
}
