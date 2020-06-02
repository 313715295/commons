package com.jdxiaokang.commons.support;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: commons
 * @description: 仅供Lambda表达式使用
 * @date 2020/6/2
 */
public interface SerializableExpression  extends Serializable {


    //Lambda编译会生成writeReplace方法，返回的即SerializedLambda对象
    default SerializedLambda getSerializedLambda() throws Exception {
        Method write = this.getClass().getDeclaredMethod("writeReplace");
        write.setAccessible(true);
        return (SerializedLambda) write.invoke(this);
    }

    /**
     * 获取实现方法信息 包括方法名称和参数信息
     * @return 描述信息
     */
    default String getImplMethodDetail() {
        try {
            SerializedLambda serializedLambda = getSerializedLambda();
            String implMethodName = serializedLambda.getImplMethodName();
            int argsCount = serializedLambda.getCapturedArgCount();
            StringBuilder args = new StringBuilder();
            for (int i = 0; i < argsCount; i++) {
                args.append(" [").append(serializedLambda.getCapturedArg(i)).append("] ");
            }
            return "实现方法名称=[" + implMethodName + "] 参数信息:【" + args.toString() + "】";
        } catch (Exception e) {
            return null;
        }
    }
}
