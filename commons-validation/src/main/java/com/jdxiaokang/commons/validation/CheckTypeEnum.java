package com.jdxiaokang.commons.validation;

public enum CheckTypeEnum {
    IS_NOT_EMPTY(1, "属性值不能为空"),

    IS_GREATER_ZERO(2, "属性值必须大于0")

    ;

    private int code;

    private String errorMessage;

    CheckTypeEnum(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode(){
        return this.code;
    }

    public String getErrorMessage(){
        return this.errorMessage;
    }
}