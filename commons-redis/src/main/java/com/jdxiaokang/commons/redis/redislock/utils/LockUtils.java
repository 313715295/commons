package com.jdxiaokang.commons.redis.redislock.utils;


import com.jdxiaokang.commons.redis.redislock.support.LockStore;
import com.jdxiaokang.commons.support.VoidAction;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: settleservice
 * @description: 加锁工具类
 * @date 2020/1/7
 */
public class LockUtils {

    /**
     * 锁工厂
     */
    private static LockStore lockStore;

    /**
     * 初始化锁工厂
     * @param lockStore 锁工厂
     */
    public static void setLockStore(LockStore lockStore) {
        LockUtils.lockStore = lockStore;
    }

    /**
     * 业务逻辑加锁处理，加锁失败忽略
     *
     * @param lockKey      锁的Key
     * @param bizAction 业务逻辑
     */
    public static void actionLock(String lockKey, VoidAction bizAction) {
        Lock lock = lockStore.getLock(lockKey);
        lock.lock();
        try {
            bizAction.action();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 业务逻辑加锁处理，加锁失败忽略
     *
     * @param lockKey      锁的Key
     * @param bizAction 业务逻辑
     * @param logAction 打印日志逻辑
     */
    public static void actionTryLock(String lockKey, VoidAction bizAction, VoidAction logAction) {
        Lock lock = lockStore.getLock(lockKey);
        if (lock.tryLock()) {
            try {
                bizAction.action();
            } finally {
                lock.unlock();
            }
        } else {
            logAction.action();
        }
    }
    /**
     * 业务逻辑加锁处理，加锁失败抛出异常
     *
     * @param  lockKey      锁的Key
     * @param bizAction 业务逻辑
     * @param logAction 打印日志逻辑
     * @param exception 异常逻辑
     * @param <X>       异常类型
     * @throws X 异常
     * @param <T> 返回数据类型
     */
    public static <X extends Throwable,T> T actionTryLock(String lockKey, Supplier<T> bizAction, VoidAction logAction, Supplier<X> exception) throws X {
        Lock lock = lockStore.getLock(lockKey);
        if (lock.tryLock()) {
            try {
                return bizAction.get();
            } finally {
                lock.unlock();
            }
        } else {
            logAction.action();
            throw exception.get();
        }
    }
    /**
     * 业务逻辑加锁处理，加锁失败抛出异常
     *
     * @param  lockKey      锁的Key
     * @param bizAction 业务逻辑
     * @param logAction 打印日志逻辑
     * @param exception 异常逻辑
     * @param <X>       异常类型
     * @throws X 异常
     */
    public static <X extends Throwable> void actionTryLock(String lockKey,  VoidAction bizAction, VoidAction logAction, Supplier<X> exception) throws X {
        Lock lock = lockStore.getLock(lockKey);
            if (lock.tryLock()) {
                try {
                    bizAction.action();
                } finally {
                    lock.unlock();
                }
            } else {
                logAction.action();
                throw exception.get();
            }
    }
}
