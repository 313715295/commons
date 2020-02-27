package com.jdxiaokang.commons.redis.redislock.config;


import com.jdxiaokang.commons.redis.redislock.registry.RedisLockRegistry;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * redis config
 *
 * @author tanyong
 * @version $Id: RedisConfig.java, v 0.1 2018年6月11日 下午2:14:16 ThinkPad Exp $
 */
@Component
public class RedisLockConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.port}")
    private Integer port;
    private static final String PREFIX = "redis://";

    private RedisLockRegistry redisLockRegistry;

    @Bean
    public RedissonClient create() {
        Config config = new Config();
        //默认连接池配置
        config
//				默认30秒。仅未设置锁时间时有效
//				.setLockWatchdogTimeout()
                .useSingleServer()
                .setAddress(PREFIX + host + ":" + port)
                .setPassword(password);
        return Redisson.create(config);
    }

    @Bean
    public RedisLockRegistry redisLockRegistry(RedissonClient redissonClient) {
        redisLockRegistry =  new RedisLockRegistry(redissonClient, "zwq:test");
        return redisLockRegistry;
    }


    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void cle() {
        redisLockRegistry.expireUnusedOlderThan(1000 * 60 * 60);
    }


}
