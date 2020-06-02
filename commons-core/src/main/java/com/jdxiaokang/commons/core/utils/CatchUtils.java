package com.jdxiaokang.commons.core.utils;


import com.jdxiaokang.commons.core.support.DubboRpcSupplier;
import com.jdxiaokang.commons.exceptions.APIServiceException;
import com.jdxiaokang.commons.exceptions.ServiceException;
import com.jdxiaokang.commons.support.VoidAction;
import com.jdxiaokang.commons.utils.ThrowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.remoting.TimeoutException;

import javax.validation.ConstraintViolationException;
import java.util.function.Supplier;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 异常捕获工具类
 * @date 2020/1/10
 */
@Slf4j
public class CatchUtils {

    /**
     * 捕获dubbo调用中的异常，做统一处理,默认服务响应超时默认抛异常
     * @param supplier 执行逻辑 并返回结果
     * @param errorMsg 返回错误消息
     */
    public static<T,X extends Throwable> T catchDubboRPCException(DubboRpcSupplier<T> supplier, String errorMsg){
        return catchDubboRPCException(supplier, () -> new ServiceException(errorMsg), true);
    }



    /**
     * 捕获dubbo调用中的异常，做统一处理
     *
     * @param supplier 执行逻辑 并返回结果
     * @param exception 返回错误
     * @param serverSide 服务端超时是否抛异常 false-不抛
     */
    public static<T,X extends Throwable> T catchDubboRPCException(DubboRpcSupplier<T> supplier, Supplier<X> exception,
                                                                  boolean serverSide) throws X{
        try {
            return supplier.get();
        } catch (TimeoutException timeoutException) {
            if (timeoutException.isServerSide()) {
                //服务超时，不可确定服务最终会不会执行，吃掉异常，记录日志
                log.error("dubbo rpc 服务响应超时，注意确认最终结果;异常=[{}]", ThrowableUtils.getCallErrorLogWithCall(timeoutException));
                if (!serverSide) {
                    return null;
                }
            } else {
                //调用超时
                log.error("dubbo rpc 服务调用超时，请检查;异常=[{}]", ThrowableUtils.getCallErrorLogWithCall(timeoutException));
            }
        } catch (Exception runtimeException) {
            Throwable cause = runtimeException.getCause();
            String implMethodDetail = supplier.getImplMethodDetail();
            if (cause instanceof ServiceException) {
                log.error("dubbo rpc 服务执行异常，方法调用信息=[{}],请检查;异常=[{}]",implMethodDetail,ThrowableUtils.getCallErrorLogWithCall(cause));
            } else {
                log.error("dubbo rpc 服务执行异常，方法调用信息=[{}],请检查;异常=[{}]",implMethodDetail,ThrowableUtils.getCallErrorLogWithCall(runtimeException));
            }
        }
        throw exception.get();
    }


    /**
     * 捕获任务的异常,并抛出API异常
     * 统一挪到dubbo全局拦截器 {@link com.jdxiaokang.commons.core.dubbo.ValidatorFilter}
     *
     * @param voidAction 执行逻辑
     */
    @Deprecated
    public static void throwAPIException(VoidAction voidAction, String errorMsg) throws APIServiceException {
        try {
            voidAction.action();
        }catch (ConstraintViolationException ce) {
            errorMsg = ValidateUtils.buildErrorMsg(ce.getConstraintViolations());
            log.info("校验参数失败:[{}]",errorMsg);
        }catch (ServiceException se){
            errorMsg = se.getMessage();
            log.info("任务执行异常:[{}]",errorMsg);
        } catch (Exception e1) {
            log.error("任务执行过程中发生异常:[{}]", ThrowableUtils.getCallErrorLogWithCall(e1));
        }
        throw new APIServiceException(errorMsg);
    }

    /**
     * 捕获任务的异常,并抛出API异常
     * 统一挪到dubbo全局拦截器 {@link com.jdxiaokang.commons.core.dubbo.ValidatorFilter}
     *
     * @param supplier 执行逻辑 并返回结果
     *
     */
    @Deprecated
    public static<T> T throwAPIException(Supplier<T> supplier, String errorMsg) throws APIServiceException {
        try {
            return supplier.get();
        }catch (ConstraintViolationException ce) {
            errorMsg = ValidateUtils.buildErrorMsg(ce.getConstraintViolations());
            log.info("校验参数失败:[{}]",errorMsg);
        }catch (ServiceException se){
            errorMsg = se.getMessage();
            log.info("任务执行异常:[{}]",errorMsg);
        } catch (Exception e1) {
            log.error("任务执行过程中发生异常:[{}]", ThrowableUtils.getCallErrorLogWithCall(e1));
        }
        throw new APIServiceException(errorMsg);
    }
}
