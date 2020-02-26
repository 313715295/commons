package com.jdxiaokang.commons.exceptions;


import static com.jdxiaokang.commons.exceptions.BaseErrorCodeEnum.SYSTEM_ERROR;

/**
 *   
 *  @Description:服务层异常类
 */
public class ServiceException extends JKangException{

    private static final long serialVersionUID = 1306929421064469489L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ServiceException(int errorCode, String message) {
        super(errorCode, message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ServiceException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
