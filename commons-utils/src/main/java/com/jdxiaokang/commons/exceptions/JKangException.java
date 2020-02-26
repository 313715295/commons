package com.jdxiaokang.commons.exceptions;

import static com.jdxiaokang.commons.exceptions.BaseErrorCodeEnum.SYSTEM_ERROR;

public class JKangException extends RuntimeException{
    private static final long serialVersionUID = 1;
    private  int errorCode=-1;

    public JKangException() {
        this(SYSTEM_ERROR);
    }

    public JKangException(String message) {
        this(SYSTEM_ERROR.getErrorCode(), message);
    }

    public JKangException(ErrorCode errorCode) {
        this(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }

    public JKangException(int errorCode , String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public JKangException(Throwable cause) {
        this(SYSTEM_ERROR, cause);
    }
    public JKangException(String message, Throwable cause) {
        this(SYSTEM_ERROR.getErrorCode(), message, cause);
    }
    public JKangException(ErrorCode errorCode,Throwable cause) {
        this(errorCode.getErrorCode(), errorCode.getErrorMsg(),cause);
    }

    public JKangException(int errorCode , String message, Throwable cause) {
        super(message,cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}