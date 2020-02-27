package com.jdxiaokang.commons.validation.constraints;



import com.jdxiaokang.commons.validation.validators.LimitIntValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: validateddemo
 * @description:
 * @date 2020/2/6
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {LimitIntValueValidator.class})
public @interface LimitIntValue {
     int[] limit();
    //如果校验不通过,提示什么信息
    String message() default "不符合规范";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
