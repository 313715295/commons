package com.jdxiaokang.redis.redislock.aspect;



import com.jdxiaokang.redis.redislock.registry.RedisLockRegistry;
import com.jdxiaokang.redis.redislock.support.LockMethodEnum;
import com.jdxiaokang.redis.redislock.support.RedisLock;
import com.jdxiaokang.redis.redislock.support.RepeatRequestException;
import com.jdxiaokang.redis.redislock.utils.AspectExpressUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: redis-lock
 * @description:
 * @date 2020/2/16
 */
@Aspect
@Component
@Slf4j
public class RedisLockAspect {

    @Autowired
    private RedisLockRegistry redisLockRegistry;


    @Pointcut("@annotation(com.jdxiaokang.redis.redislock.support.RedisLock)")
    public void redisLock() {
    }


    /**
     * 用户请求单次限制,需要登陆
     */
    @Around(value = "redisLock()&&@annotation(lock)", argNames = "joinPoint,lock")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock lock) throws Throwable {
        String express = "#{T("+ lock.generatorKeyClass().getName() +")."+lock.lockKey()+"}";
        String key = AspectExpressUtil.getValue(express, joinPoint);
        LockMethodEnum lockMethodEnum = lock.lockMethod();
        if (lockMethodEnum == LockMethodEnum.LOCK) {
            return lock(joinPoint, redisLockRegistry.obtain(key));
        }
        if (lockMethodEnum == LockMethodEnum.TRY_LOCK) {
            return tryLock(joinPoint, redisLockRegistry.obtain(key));
        }
        if (lockMethodEnum == LockMethodEnum.TRY_LOCK_ELSE_THROWS) {
            return tryLockElseThrows(joinPoint, redisLockRegistry.obtain(key));
        }
        throw new RuntimeException("加锁方式错误");
    }


    private Object lock(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        lock.lock();
        try {
           return joinPoint.proceed();
        }finally {
            lock.unlock();
        }
    }

    private Object tryLock(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable{
        if (lock.tryLock()) {
            try {
                return joinPoint.proceed();
            }finally {
                lock.unlock();
            }
        }
        log.info("业务类=[{}] 方法=[{}] 正在加锁处理中，lock=[{}]],忽略重复请求", joinPoint.getTarget().getClass().getName(),
                joinPoint.getSignature().getName(),lock);
        return null;
    }
    private Object tryLockElseThrows(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable{
        if (lock.tryLock()) {
            try {
                return joinPoint.proceed();
            }finally {
                lock.unlock();
            }
        }
        log.info("业务类=[{}] 方法=[{}] 正在加锁处理中，lock=[{}]],忽略重复请求", joinPoint.getTarget().getClass().getName(),
                joinPoint.getSignature().getName(),lock);
        throw new RepeatRequestException("请求处理中,请稍后");
    }
}
