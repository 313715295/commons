package com.jdxiaokang.commons.utils;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 针对错误异常的工具
 * @date 2020/1/3
 */
public class ThrowableUtils {

    /**
     * 获取调用方法打印日志所需的信息
     * @return 错误信息
     */
    public static String getCallErrorLogWithCall(Throwable throwable) {
        //打印格式: thread | class | methodName  任务日志已经包含了前面的，现在只需返回方法名称 和行号 异常类型和消息
        StackTraceElement[] arr = throwable.getStackTrace();
        StringBuilder builder = new StringBuilder();
        //打印堆栈层数
        builder.append(throwable.toString()).append(':').append(throwable.getMessage()).append("\r\n");
        for (StackTraceElement element : arr) {
            builder.append("\tat ").append(element.toString()).append("\r\n");
        }
        //todo 后期判断cause 是否和异常相同，如果不相同，把cause也打印完
        return builder.toString();
    }
    /**
     * 获取当前方法打印日志所需的信息
     * @return 错误信息
     */
    public static String getErrorLog(Throwable throwable) {
        StackTraceElement[] arr = throwable.getStackTrace();
        //打印格式: thread | class | methodName  任务日志已经包含了前面的，现在只需返回方法名称 和行号 异常类型和消息
        return '(' + arr[0].getMethodName() + ":" + arr[0].getLineNumber() + ")|" + throwable.toString() + ':' + throwable.getMessage();
    }




}
