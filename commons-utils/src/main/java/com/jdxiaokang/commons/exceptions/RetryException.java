package com.jdxiaokang.commons.exceptions;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: parent
 * @description:  需要重试的操作 可以产生该异常用以捕获
 * @date 2020/2/25
 */
public class RetryException extends RuntimeException {

    public RetryException() {
    }

    public RetryException(String message) {
        super(message);
    }
}
