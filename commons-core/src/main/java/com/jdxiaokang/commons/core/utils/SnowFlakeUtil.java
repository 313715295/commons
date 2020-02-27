package com.jdxiaokang.commons.core.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: commons
 * @description: 雪花算法
 * @date 2020/2/26
 */
@Slf4j
public class SnowFlakeUtil {

    /**
     * 初始时间戳 2020-02-02T00:00
     */
    private static final long START_STAMP = 1580572800000L;
    /**
     * 机器号码长度 64 0-63
     */
    private static final long WORKER_ID_BITS = 6L;
    /**
     *  数据中心号码长度 0-15
     */
    private static final long DATA_CENTER_ID_BITS = 4L;
    /**
     * 机器最大数
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /**
     * 数据中心最大数
     */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    /**
     * 序列号长度
     */
    private static final long SEQUENCE_BITS = 12L;
    /**
     * 序列号最大值
     */
    private static final long SEQUENCE_MAK = ~(-1L << SEQUENCE_BITS);

    /**
     * 工作id需要左移的位数，12位
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * 数据id需要左移位数 12+5=17位
     */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /**
     * 时间戳需要左移位数 12+5+5=22位
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * 上次时间戳，初始值为起始值
     */
    private static long lastTimestamp = START_STAMP;
    /**
     * 机器码 默认1
     */
    public static long workerId = 1L;
    /**
     * 数据中心 默认1
     */
    public static int dataCenterId = 1;

    /**
     * 序列号
     */
    private static long sequence = 0;

    /**
     * 初始化机器码，数据中心暂时默认1
     * @param workerId 机器码
     */
    public static void initWorkId(long workerId) {
        if (workerId > MAX_WORKER_ID) {
            throw new RuntimeException("超过最大机器码");
        }
        SnowFlakeUtil.workerId = workerId;
    }

    /**
     * 下一个ID
     */
    public static synchronized long nextId() {
        long timestamp = timeGen();
        //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (timestamp < lastTimestamp) {
            log.error("clock is moving backwards.  Rejecting requests until [{}]", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }
        //获取当前时间戳如果等于上次时间戳（同一毫秒内），则在序列号加一；否则序列号赋值为0，从0开始。
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MAK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        //将上次时间戳值刷新
        lastTimestamp = timestamp;


        return ((timestamp - START_STAMP) << TIMESTAMP_LEFT_SHIFT) |
                (dataCenterId << DATA_CENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    //获取时间戳，并与上次时间戳比较
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    public static long timeGen(){
        return System.currentTimeMillis();
    }

}
