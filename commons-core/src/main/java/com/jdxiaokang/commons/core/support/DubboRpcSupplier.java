package com.jdxiaokang.commons.core.support;

import org.apache.dubbo.remoting.TimeoutException;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description:
 * @date 2020/1/13
 */
public interface DubboRpcSupplier<T> {

    T get() throws TimeoutException;

}
