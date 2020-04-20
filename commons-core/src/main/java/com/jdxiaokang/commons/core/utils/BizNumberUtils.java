package com.jdxiaokang.commons.core.utils;

import com.jdxiaokang.commons.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.jdxiaokang.commons.utils.DateUtils.TIME_WITH_MILLI_PATTERN;


/**
 * 业务数字工具类，基础写最下面， 分类型分开写
 */
public class BizNumberUtils {

    /**
     * 默认业务code
     */
    public static final String DEFAULT_BID = "000";
    /**
     * 以下分别为把时间戳转为long所需的数字相乘
     */
    private static final long YEAR_OF_TIME = 10000000000000L;
    private static final long MONTH_OF_TIME = 100000000000L;
    private static final long DAY_OF_TIME = 1000000000L;
    private static final int HOUR_OF_TIME = 10000000;
    private static final int MINUTE_OF_TIME = 100000;
    private static final int SECOND_OF_TIME = 1000;

    /**
     * 四舍五入 保留两位数
     */
    public static BigDecimal halfUp(BigDecimal number) {
        return Optional.ofNullable(number).map(n->n.setScale(2, BigDecimal.ROUND_HALF_UP)).orElse(BigDecimal.ZERO);
    }
    /**
     * 四舍五入
     * @param scale 小数位
     */
    public static BigDecimal halfUp(BigDecimal number, int scale) {
        return Optional.ofNullable(number).map(n->n.setScale(scale, BigDecimal.ROUND_HALF_UP)).orElse(BigDecimal.ZERO);
    }

    /**
     * 正数舍,负数进,保留2位小数
     */
    public static BigDecimal roundFloor(BigDecimal number) {
        return Optional.ofNullable(number).map(n -> n.setScale(2, BigDecimal.ROUND_FLOOR)).orElse(BigDecimal.ZERO);
    }
    /**
     * 我方两个数据相乘，采用正数进,负数舍,保留2位小数
     *
     */
    public static BigDecimal ourMultiply(@Nonnull BigDecimal a, @Nonnull BigDecimal b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return a.multiply(b).setScale(2, BigDecimal.ROUND_CEILING);
    }

    /**
     * 他方两个数据相乘，采用正数舍,负数进,保留2位小数
     */
    public static BigDecimal otherMultiply(@Nonnull BigDecimal a, @Nonnull BigDecimal b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return a.multiply(b).setScale(2, BigDecimal.ROUND_FLOOR);
    }
    /**
     * 四舍五入 保留2位小数
     */
    public static BigDecimal halfUpMultiply(@Nonnull BigDecimal a, @Nonnull BigDecimal b) {
        return halfUpMultiply(a, b, 2);
    }
    /**
     * 四舍五入
     */
    public static BigDecimal halfUpMultiply(@Nonnull BigDecimal a, @Nonnull BigDecimal b,int newScale) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return a.multiply(b).setScale(newScale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 四舍五入 保留2位小数
     * @param value 被除数
     * @param divisor 除数
     */
    public static BigDecimal halfUpDivide(@Nonnull BigDecimal value, @Nonnull BigDecimal divisor) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(divisor);
        return value.divide(divisor,2,BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 根据时间返回标识，例如2019-12-05，返回20191205
     *
     * @param date 日期
     * @return 标识
     */
    public static int createNumByDate(@Nonnull LocalDate date) {
        Objects.requireNonNull(date);
        int year = date.getYear(), month = date.getMonthValue(), day = date.getDayOfMonth();
        return year * 10000 + month * 100 + day;
    }

    /**
     * 根据时间返回标识，例如2019-12-05 12:12:12:555，返回20191205121212555
     *
     * @param date 日期
     * @return 标识
     */
    public static Long createNumByDateTime(@Nonnull LocalDateTime date) {
        Objects.requireNonNull(date);
        int year = date.getYear(), month = date.getMonthValue(), day = date.getDayOfMonth();
        int hour = date.getHour(), minute = date.getMinute(), second = date.getSecond();
        int mille = date.getNano() / 1000000;
        return year * YEAR_OF_TIME + month * MONTH_OF_TIME + day * DAY_OF_TIME + hour * HOUR_OF_TIME + MINUTE_OF_TIME * minute
                + second * SECOND_OF_TIME + mille;
    }
    /**
     * 根据时间返回标识，例如2019-12-05 12:12:12:555，返回20191205121212555
     *
     * @param date 日期
     * @return 标识
     */
    public static String createNumByLocalDateTime(@Nonnull LocalDateTime date) {
        Objects.requireNonNull(date);
        return DateUtils.localDateTimeToString(date, TIME_WITH_MILLI_PATTERN);
    }

    /**
     * 生成long类型的id  雪花算法
     */
    public static long createId() {
        return SnowFlakeUtil.nextId();
    }
    /**
     * 生成long类型的id  雪花算法
     */
    public static String createApplyId() {
        return String.valueOf(createId());
    }
    /**
     * 根据业务code和当前时间戳生成业务id  差不多单线程一秒150万个
     * @return bid
     */
    public static String createBId(String serviceCode) {
        return createBId(serviceCode, LocalDateTime.now());
    }

    /**
     * 根据业务code和时间戳生成业务id
     * @param dateTime 时间戳 不传则为当前时间戳
     * @return bid
     */
    public static String createBId(String serviceCode, LocalDateTime dateTime) {
        if (StringUtils.isBlank(serviceCode)) {
            serviceCode = DEFAULT_BID;
        }
        dateTime = Optional.ofNullable(dateTime).orElse(LocalDateTime.now());
        return BIdGenerator.createBId(serviceCode,dateTime);
    }



}
