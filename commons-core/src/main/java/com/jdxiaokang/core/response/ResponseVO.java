package com.jdxiaokang.core.response;

import com.jdxiaokang.commons.exceptions.ErrorCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "返回结果")
@Accessors(chain = true)
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = -172911448358060648L;

    /**
     * 接口请求id
     */
    @ApiModelProperty(value = "接口请求id", example = "1577831615367")
    private Long requestId = System.currentTimeMillis();

    /**
     * 状态(true:成功,false:失败)
     */
    @ApiModelProperty(value = "状态(true:成功,false:失败)")
    private boolean status = false;

    /**
     * 错误提示
     */
    @ApiModelProperty(value = "错误提示", example = "查询数据失败")
    private String message;

    /**
     * 异常信息（不提示给用户） 便于抓包排查问题，特别是未知系统异常，一般在debug模式下启用
     */
    @ApiModelProperty(value = "异常信息", example = "异常信息")
    private String exception;

    /**
     * 响应码
     */
    @ApiModelProperty(value = "响应码", example = "0")

    private long responseCode;

    /**
     * 响应时间
     */
    @ApiModelProperty(value = "响应时间", example = "1577831615367")
    private long timestamp = System.currentTimeMillis();

    /**
     * 业务数据
     */
    @ApiModelProperty(value = "业务数据")
    private T entry;

    /**
     * 记录总（分页时有用）
     */
    @ApiModelProperty(value = "记录总数", example = "22")
    private long totalRecordSize;

    /**
     * 是否还有下一页
     */
    @ApiModelProperty(value = "是否还有下一页")
    private boolean isHasNext;

    public ResponseVO() {
    }


    public static <T> ResponseVO<T> success() {
        return new ResponseVO<T>().succeed();
    }

    public static <T> ResponseVO<T> success(T entry) {
        return new ResponseVO<T>().succeed(entry);
    }

    public static <T> ResponseVO<T> success(int responseCode, String message) {
        return new ResponseVO<T>().succeed(responseCode, message);
    }

    public static <T> ResponseVO<T> success(int responseCode, String message, T entry) {
        return new ResponseVO<T>().succeed(responseCode, message, entry);
    }

    public static <T> ResponseVO<T> failure(ErrorCode errorCode) {
        return new ResponseVO<T>().fail(errorCode);
    }

    public static <T> ResponseVO<T> failure(String errorMsg) {
        return new ResponseVO<T>().fail(errorMsg);
    }

    public static <T> ResponseVO<T> failure(int responseCode, String errorMsg) {
        return new ResponseVO<T>().fail(responseCode, errorMsg);
    }

    public static <T> ResponseVO<T> failure( int responseCode,String errorMsg,String exception) {
        return new ResponseVO<T>().fail(responseCode,errorMsg,exception);
    }






    public ResponseVO<T> succeed() {
       return succeed(DEFAULT_SUCCESS.getErrorCode(), DEFAULT_SUCCESS.getErrorMsg(), null);
    }


    public ResponseVO<T> succeed(T entry) {
        return succeed(DEFAULT_SUCCESS.getErrorCode(), DEFAULT_SUCCESS.getErrorMsg(), entry);
    }

    public ResponseVO<T> succeed(int responseCode, String message) {
        return succeed(responseCode, message,null);
    }

    @SuppressWarnings("unchecked")
    public ResponseVO<T> succeed(int responseCode, String message, T entry) {
        this.status = true;
        this.responseCode = responseCode;
        this.message = message;
        this.entry = Optional.ofNullable(entry).orElse((T)new HashMap<>());
        return this;
    }


    public ResponseVO<T> fail(String errorMsg) {
        return fail(SYSTEM_ERROR.getErrorCode(), errorMsg);
    }

    public ResponseVO<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }

    public ResponseVO<T> fail(int responseCode, String errorMsg) {
        return fail(responseCode, errorMsg, null);
    }

    public ResponseVO<T> fail(int responseCode, String errorMsg, String exception) {
        this.status = false;
        this.responseCode = responseCode;
        this.message = errorMsg;
        this.exception = exception;
        return this;
    }


}
