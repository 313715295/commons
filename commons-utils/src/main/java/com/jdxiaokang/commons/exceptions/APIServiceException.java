package com.jdxiaokang.commons.exceptions;


/**
 *   
 *  @Description:API实现中会抛出的异常
 */
public class APIServiceException extends ServiceException {

    private static final long serialVersionUID = 1306929421064469489L;

    public APIServiceException() {
    }

    public APIServiceException(String message) {
        super(message);
    }

    public APIServiceException(ErrorCode errorCode) {
        super(errorCode);
    }


}
