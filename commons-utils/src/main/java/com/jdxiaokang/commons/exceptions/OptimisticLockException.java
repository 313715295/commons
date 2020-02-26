package com.jdxiaokang.commons.exceptions;


/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: parent
 * @description:  由乐观锁类引起的异常，可以考虑重试
 * @date 2020/2/25
 */
public class OptimisticLockException extends RetryException{

    private static final long serialVersionUID = 1306929421064469489L;

    public OptimisticLockException() {
    }

    public OptimisticLockException(String message) {
        super(message);
    }
}
