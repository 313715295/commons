package com.jdxiaokang.commons.utils;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 数字类工具
 * @date 2020/1/8
 */
public class NumberUtils {

    /**
     * 大于零
     */
    public static boolean gtZero(BigDecimal bigDecimal) {
        return Optional.ofNullable(bigDecimal).map(value -> value.compareTo(BigDecimal.ZERO) > 0).orElse(Boolean.FALSE);
    }

    /**
     * 大于等于零
     */
    public static boolean geZero(BigDecimal bigDecimal) {
        return Optional.ofNullable(bigDecimal).map(value -> value.compareTo(BigDecimal.ZERO) >= 0).orElse(Boolean.FALSE);
    }

    /**
     * 等于零
     */
    public static boolean eqZero(BigDecimal bigDecimal) {
        return Optional.ofNullable(bigDecimal).map(value -> value.compareTo(BigDecimal.ZERO) == 0).orElse(Boolean.FALSE);
    }

    /**
     * 小于零
     */
    public static boolean ltZero(BigDecimal bigDecimal) {
        return Optional.ofNullable(bigDecimal).map(value -> value.compareTo(BigDecimal.ZERO) < 0).orElse(Boolean.FALSE);
    }

    /**
     * 小于等于零
     */
    public static boolean leZero(BigDecimal bigDecimal) {
        return Optional.ofNullable(bigDecimal).map(value -> value.compareTo(BigDecimal.ZERO) <= 0).orElse(Boolean.FALSE);
    }

    /**
     * 判断传入的数据不为0
     *
     * @param bigDecimal 数据
     */
    public static boolean notZero(BigDecimal bigDecimal) {
        return Optional.ofNullable(bigDecimal).map(value -> value.compareTo(BigDecimal.ZERO) != 0).orElse(Boolean.FALSE);
    }


    /**
     * 检查传入的字符是否为数字且大于0
     *
     * @param num 字符
     */
    public static boolean isPositive(String num) {
        if (org.apache.commons.lang3.math.NumberUtils.isCreatable(num)) {
            return Double.parseDouble(num) > 0;
        }
        return false;
    }

    /**
     * 检查传入数字是否大于0
     *
     * @param num number
     * @return 大于0返回true
     */
    public static boolean isPositive(Number num) {
        if (num == null) {
            return false;
        }
        return num.doubleValue() > 0;
    }


}
