package com.jdxiaokang.commons.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.jdxiaokang.commons.core.utils.BizNumberUtils.createNumByDateTime;


/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description:  仿雪花，时间戳改为当前时间
 * @date 2020/1/3
 */
@Slf4j
public class BIdGenerator {

    /**
     * 记录上一个操作者的时间戳
     */
    private static long lastTimestamp = -1L;
    /**
     * 机器ID 2位数，先占位，如果冲突多，再做全局注册
     */
    private static int machineId = 10;

    private static String machineIdStr = machineId+"";

    /**
     * 序列号
     */
    private static int sequence = 0;

    /**
     * 序列号长度 采取序列号长度+序列号 暂时先相加，后续如果要缩短再考虑
     */
    private static final int SEQUENCE_LENGTH = 10000;

    public static void initMachineId(int machineId) {
        if (machineId < 0 || machineId > 99) {
            throw new RuntimeException("机器码不符合");
        }
        BIdGenerator.machineId = machineId;
        if (machineId < 10) {
            machineIdStr = "0" + machineId;
        } else {
            machineIdStr = String.valueOf(machineId);
        }
    }
    /**
     * 生成全局唯一的业务id
     * @param serviceCode 业务code
     * @param dateTime 时间戳
     */
    public static String createBId(String serviceCode, LocalDateTime dateTime) {
        long base = createNumByDateTime(dateTime);
        int sequence = SEQUENCE_LENGTH+nextId(base);

        return base + serviceCode+machineIdStr+ sequence;
    }

    /**
     * 根据时间戳来判断生成下一个序列，暂时先用synchronized
     *
     * @param timestamp 时间戳
     * @return 序列号
     */
    private static synchronized int nextId(long timestamp) {
        //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (timestamp < lastTimestamp) {
            log.error("clock is moving backwards.  Rejecting requests until [{}]", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }
        if (timestamp == lastTimestamp) {
            //序列最大值2的13次方-1，大于8191则可能重复，但一毫秒8191的可能性很少
            int sequenceMask = 8191;
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
               //序列号不够用，该次生成失败，可能性很小，因为前面已经提前生成好时间戳了。
                throw new RuntimeException("生成id失败");
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return sequence;
    }

}
