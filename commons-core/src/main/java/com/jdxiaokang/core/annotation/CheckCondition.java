package com.jdxiaokang.core.annotation;


import com.jdxiaokang.core.validated.CheckTypeEnum;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME )
public @interface CheckCondition {
    /**
     * 检查类型
     * @return
     */
    CheckTypeEnum checkType();

    /**
     * 错误提示内容
     * @return
     */
    String errorContent() default "";
}
