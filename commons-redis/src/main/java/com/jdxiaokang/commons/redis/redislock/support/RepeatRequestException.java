package com.jdxiaokang.commons.redis.redislock.support;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: redis-lock
 * @description: 重复请求异常
 * @date 2020/2/16
 */
public class RepeatRequestException extends RuntimeException  {

    public RepeatRequestException() {
    }

    public RepeatRequestException(String message) {
        super(message);
    }

    public RepeatRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepeatRequestException(Throwable cause) {
        super(cause);
    }

    public RepeatRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
