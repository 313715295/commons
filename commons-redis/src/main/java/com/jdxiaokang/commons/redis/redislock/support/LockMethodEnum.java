package com.jdxiaokang.commons.redis.redislock.support;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: redis-lock
 * @description: 加锁方式
 * @date 2020/2/16
 */
public enum LockMethodEnum {

    LOCK,TRY_LOCK,TRY_LOCK_ELSE_THROWS;
}
