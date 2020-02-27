package com.jdxiaokang.commons.redis.redislock.support;

import java.lang.annotation.*;

/**
 * @author zwq  wenqiang.zheng@jdxiaokang.cn
 * @project: redis-lock
 * @description: 分布式锁注解
 * @date 2020/2/16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    /**
     * 加锁key,使用spel表达式 例:method(#a0)
     * method为传入的构建锁的类的静态方法
     * 第一个参数的值为a0,以此类推。
     */
    String lockKey() default "";

    /**
     * 构建锁的类
     */
    Class<?> generatorKeyClass();

    /**
     * 加锁方式
     */
    LockMethodEnum lockMethod() default LockMethodEnum.TRY_LOCK;

}
