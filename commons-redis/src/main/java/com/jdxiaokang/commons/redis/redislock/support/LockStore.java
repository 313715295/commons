package com.jdxiaokang.commons.redis.redislock.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * @author Administrator
 * @Description
 * @create 2019-07-16 16:49
 */
public interface LockStore {
    /**
     * 存放锁对象
     */
    Map<String, Lock> LOCK_FACTORY = new ConcurrentHashMap<>();
    /**
     * 数据库加悲观锁字段
     */
    String LOCK_SQL = " for update ";

    /**
     * 获取锁
     * @param key key
     * @return 锁
     */
    Lock getLock(String key);

    /**
     * 定时清除所对象
     */
    default void clearLock(){};
}
