package com.jdxiaokang.commons.exceptions;

public enum BaseErrorCodeEnum implements ErrorCode {
    //错误码
    SYSTEM_ERROR(1001, "系统处理失败"),
    PARAM_EMPTY(1002,"参数为空"),
    DEFAULT_SUCCESS(0, "success"),
 ;

    private int errorCode;

    private String errorMsg;

    BaseErrorCodeEnum(int errorCode, String errorMsg) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
