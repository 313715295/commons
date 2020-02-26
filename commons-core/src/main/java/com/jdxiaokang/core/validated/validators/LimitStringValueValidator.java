package com.jdxiaokang.core.validated.validators;

import com.jdxiaokang.core.validated.constraints.LimitStringValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: validateddemo
 * @description: 限定值
 * @date 2020/2/6
 */
@Slf4j
public class LimitStringValueValidator implements ConstraintValidator<LimitStringValue, String> {

    private String[] limit;
    @Override
    public void initialize(LimitStringValue limitIntValue) {
        //初始化方法,拿到注解
        limit = limitIntValue.limit();
    }



    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isNotBlank(value)) {
            return ArrayUtils.contains(limit, value);
        }
        //传值才验证
        return true;
    }
}
