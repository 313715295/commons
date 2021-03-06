package com.jdxiaokang.commons.core.dubbo;

import com.jdxiaokang.commons.core.utils.ValidateUtils;
import com.jdxiaokang.commons.exceptions.APIServiceException;
import com.jdxiaokang.commons.exceptions.BaseErrorCodeEnum;
import com.jdxiaokang.commons.exceptions.ServiceException;
import com.jdxiaokang.commons.pojo.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ConfigUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.support.RpcUtils;
import org.apache.dubbo.validation.Validation;
import org.apache.dubbo.validation.Validator;

import javax.validation.ConstraintViolationException;

import java.util.Arrays;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;
import static org.apache.dubbo.common.constants.FilterConstants.VALIDATION_KEY;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: commons-parent
 * @description:
 * @date 2020/2/28
 */
@Slf4j
@Activate(group = {CONSUMER, PROVIDER}, value = "jd-validation", order = 10000)
public class ValidatorFilter implements Filter {

    private Validation validation;

    public ValidatorFilter() {
    }

    /**
     * Sets the validation instance for ValidationFilter
     *
     * @param validation Validation instance injected by dubbo framework based on "validation" attribute value.
     */
    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    private Class<?> returnType = ResponseDTO.class;
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcInvocation rpcInvocation = (RpcInvocation) invocation;
        String arguments = Arrays.toString(invocation.getArguments());
        String methodName = invocation.getMethodName();
        Class<?> methodReturnType = RpcUtils.getReturnType(invocation);
        if (validation != null && !invocation.getMethodName().startsWith("$")
                && ConfigUtils.isNotEmpty(invoker.getUrl().getMethodParameter(invocation.getMethodName(), VALIDATION_KEY))) {
            try {
                Validator validator = validation.getValidator(invoker.getUrl());
                if (validator != null) {
                    validator.validate(invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
                }
            } catch (RpcException e) {
                log.info("方法=[{}] 参数={} 发生异常:",methodName,arguments,e);
                throw e;
            } catch (ConstraintViolationException exception) {
                String errorMsg = ValidateUtils.buildErrorMsg(exception.getConstraintViolations());
                log.info("方法=[{}] 参数={} 发生异常:[{}]",methodName,arguments,errorMsg);
                if (returnType.equals(methodReturnType)) {
                    return AsyncRpcResult
                            .newDefaultAsyncResult(new ResponseDTO<>()
                                    .fail(errorMsg), invocation);
                }
                return AsyncRpcResult.newDefaultAsyncResult(new APIServiceException(errorMsg), invocation);
            } catch (Throwable t) {
                log.info("方法=[{}] 参数={} 发生异常:",methodName,arguments,t);
                return AsyncRpcResult.newDefaultAsyncResult(t, invocation);
            }
        }
        Result result = invoker.invoke(invocation);
        if (result.hasException()) {
            ResponseDTO<?> responseDTO;
            Throwable throwable = result.getException();
            if (throwable instanceof ServiceException) {
                ServiceException exception = (ServiceException) throwable;
                responseDTO = ResponseDTO.failure((exception.getErrorCode()), exception.getMessage());
                log.info("方法=[{}] 参数={} 发生异常:[{}]", methodName, Arrays.toString(rpcInvocation.getArguments()),
                        exception.getMessage());
            } else {
                responseDTO = ResponseDTO.failure(BaseErrorCodeEnum.SYSTEM_ERROR);
                log.info("方法=[{}] 参数={} 发生异常:", methodName,Arrays.toString(rpcInvocation.getArguments()),
                        throwable);
            }
            if (returnType.equals(methodReturnType)) {
                return AsyncRpcResult.newDefaultAsyncResult(responseDTO, invocation);
            }
        }
        return result;
    }

}


