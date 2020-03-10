package com.jdxiaokang.commons.pojo;

import com.jdxiaokang.commons.exceptions.ErrorCode;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

import static com.jdxiaokang.commons.exceptions.BaseErrorCodeEnum.DEFAULT_SUCCESS;
import static com.jdxiaokang.commons.exceptions.BaseErrorCodeEnum.SYSTEM_ERROR;


/**
 *   
 *  @Description: 返回结果
 *  @author dadi  litu@51xianqu.net
 *  @date 18/11/11  
 */
@Data
@ToString
@Accessors(chain = true)
public class ResponseDTO<T> implements Serializable {

    private static final long serialVersionUID = -172911448358060648L;

    /**
     * 接口请求id
     */
    private Long requestId = System.currentTimeMillis();

    /**
     * 状态(true:成功,false:失败)
     */
    private boolean success = false;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 异常信息（不提示给用户） 便于抓包排查问题，特别是未知系统异常，一般在debug模式下启用
     */
    private String exception;

    /**
     * 响应码
     */
    private long responseCode;

    /**
     * 响应时间
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 业务数据
     */
    private T entry;

    /**
     * 记录总（分页时有用）
     */
    private long totalRecordSize;

    /**
     * 是否还有下一页
     */
    private boolean isHasNext;

    public ResponseDTO() {
    }


    public static <T> ResponseDTO<T> success() {
        return new ResponseDTO<T>().succeed();
    }

    public static <T> ResponseDTO<T> success(T entry) {
        return new ResponseDTO<T>().succeed(entry);
    }

    public static <T> ResponseDTO<T> success(int responseCode, String message) {
        return new ResponseDTO<T>().succeed(responseCode, message);
    }

    public static <T> ResponseDTO<T> success(int responseCode, String message, T entry) {
        return new ResponseDTO<T>().succeed(responseCode, message, entry);
    }

    public static <T> ResponseDTO<T> failure(ErrorCode errorCode) {
        return new ResponseDTO<T>().fail(errorCode);
    }

    public static <T> ResponseDTO<T> failure(String errorMsg) {
        return new ResponseDTO<T>().fail(errorMsg);
    }

    public static <T> ResponseDTO<T> failure(int responseCode, String errorMsg) {
        return new ResponseDTO<T>().fail(responseCode, errorMsg);
    }

    public static <T> ResponseDTO<T> failure( int responseCode,String errorMsg,String exception) {
        return new ResponseDTO<T>().fail(responseCode,errorMsg,exception);
    }






    public ResponseDTO<T> succeed() {
       return succeed(DEFAULT_SUCCESS.getErrorCode(), DEFAULT_SUCCESS.getErrorMsg(), null);
    }


    public ResponseDTO<T> succeed(T entry) {
        return succeed(DEFAULT_SUCCESS.getErrorCode(), DEFAULT_SUCCESS.getErrorMsg(), entry);
    }

    public ResponseDTO<T> succeed(int responseCode, String message) {
        return succeed(responseCode, message,null);
    }

    @SuppressWarnings("unchecked")
    public ResponseDTO<T> succeed(int responseCode, String message, T entry) {
        this.success = true;
        this.responseCode = responseCode;
        this.message = message;
        this.entry = Optional.ofNullable(entry).orElse((T)new HashMap<>());
        return this;
    }


    public ResponseDTO<T> fail(String errorMsg) {
        return fail(SYSTEM_ERROR.getErrorCode(), errorMsg);
    }

    public ResponseDTO<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }

    public ResponseDTO<T> fail(int responseCode, String errorMsg) {
        return fail(responseCode, errorMsg, null);
    }

    public ResponseDTO<T> fail(int responseCode, String errorMsg, String exception) {
        this.success = false;
        this.responseCode = responseCode;
        this.message = errorMsg;
        this.exception = exception;
        return this;
    }


}
