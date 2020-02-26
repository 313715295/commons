package com.jdxiaokang.core.validated.validators;

import com.jdxiaokang.core.validated.constraints.LimitIntValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: validateddemo
 * @description: 限定值
 * @date 2020/2/6
 */
@Slf4j
public class LimitIntValueValidator implements ConstraintValidator<LimitIntValue, Integer> {

    private int[] limit;
    @Override
    public void initialize(LimitIntValue limitIntValue) {
        //初始化方法,拿到注解
        limit = limitIntValue.limit();
    }



    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value != null) {
            return ArrayUtils.contains(limit, value);
        }
        //传值才验证
        return true;
    }
}
