package com.jdxiaokang.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * 时间转换
 */
public class DateUtils {

    /**
     * 一天末尾时间，开始时间LocalTime.min
     */
    public static final LocalTime END_TIME_OF_DAY = LocalTime.of(23,59,59);

    /**
     * 时间转换为24小时制,例: 2018-06-27 15:24:21
     */
    public static final DateTimeFormatter FULL_CHINESE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 转换时间为时间戳的格式，例 202001021220333
     */
    public static final DateTimeFormatter TIME_WITH_MILLI_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    /**
     * 转换时间为14位定长字符串
     */
    public static final DateTimeFormatter FOURTEEN_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    /**
     * 转换年月日的格式 例 2020-01-02
     */
    public static final DateTimeFormatter LOCAL_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取当天开始 00：00：00
     * @param localDateTime  时间
     */
    public static LocalDateTime getStartOfDay(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTime -> LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN))
                .orElse(null);
    }
    /**
     * 获取当天结束 23：59：59
     * @param localDateTime  时间
     */
    public static LocalDateTime getEndOfDay(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTime -> LocalDateTime.of(localDateTime.toLocalDate(),END_TIME_OF_DAY))
                .orElse(null);
    }
    /**
     * 获取当天结束 23：59：59
     * @param localDate  时间
     */
    public static LocalDateTime getEndOfDay(LocalDate localDate) {
        return Optional.ofNullable(localDate).map(dateTime -> LocalDateTime.of(localDate,END_TIME_OF_DAY))
                .orElse(null);
    }
    /**
     * 字符串转LocalDate
     * @param text 字符文本
     * @param formatter 转换格式
     * @return LocalDateTime
     */
    public static LocalDate localDateParseFromString(String text, DateTimeFormatter formatter) {
        return Optional.ofNullable(text)
                .filter(StringUtils::isNotBlank)
                .map(t -> LocalDate.parse(t, formatter)).orElse(null);
    }

    /**
     * 字符串转LocalDateTime
     * @param text 字符文本
     * @param formatter 转换格式
     * @return LocalDateTime
     */
    public static LocalDateTime parseFromString(String text, DateTimeFormatter formatter) {
        return Optional.ofNullable(text)
                .filter(StringUtils::isNotBlank)
                .map(t -> LocalDateTime.parse(t, formatter)).orElse(null);
    }

    /**
     * LocalDateTime转时间字符串
     * @param formatter 转换格式
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return Optional.ofNullable(localDateTime).map(time-> time.format(formatter)).orElse(null);
    }

    /**
     *  转换时间
     */
    public static Date changeFromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


}
