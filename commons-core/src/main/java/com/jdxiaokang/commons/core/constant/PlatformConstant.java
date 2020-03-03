package com.jdxiaokang.commons.core.constant;


import java.math.BigDecimal;

/**
 * 平台相关常量
 */
public class PlatformConstant {

    /**
     * 系统账户公司Id
     */
    public static long SYSTEM_COMPANY_ID;

    /**
     * 平台服务费比率，2.5%
     */
    public static final BigDecimal SERVICE_FEE_RATIO = new BigDecimal("0.025");

    /**
     * 默认利率需要值除以100
     */
    public static final BigDecimal RATE_DIVISOR = BigDecimal.valueOf(100);


}
