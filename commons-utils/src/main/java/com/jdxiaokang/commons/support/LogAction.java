package com.jdxiaokang.commons.support;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 打印日志
 * @date 2020/1/4
 */
@FunctionalInterface
public interface LogAction {

    void log(Throwable e);
}
