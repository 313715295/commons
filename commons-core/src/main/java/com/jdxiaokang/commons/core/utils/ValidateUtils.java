package com.jdxiaokang.commons.core.utils;

import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: commons-parent
 * @description:
 * @date 2020/2/25
 */
public class ValidateUtils {

    public static String buildErrorMsg(Set<ConstraintViolation<?>> errorViolations) {
        String exception = "";
        if (!CollectionUtils.isEmpty(errorViolations)) {
            exception = "【" +
                    errorViolations.stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.joining(",")) +
                    "】";
        }
        return exception;
    }
    public static String buildErrorMsg(List<FieldError> fieldErrorList) {
        String exception = "";
        if (!CollectionUtils.isEmpty(fieldErrorList)) {
            exception = "【" +
                    fieldErrorList.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(",")) +
                    "】";
        }
        return exception;
    }
}
