package com.jdxiaokang.commons.core.support;

import com.jdxiaokang.commons.support.SerializableExpression;
import org.apache.dubbo.remoting.TimeoutException;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description:
 * @date 2020/1/13
 */
public interface DubboRpcSupplier<T> extends SerializableExpression {

    T get() throws TimeoutException;

}
