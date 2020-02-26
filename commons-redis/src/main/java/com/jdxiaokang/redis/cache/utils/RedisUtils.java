
package com.jdxiaokang.redis.cache.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redisDAO
 *
 * @author litu
 * @date 2018/8/31
 */
@Component
public class RedisUtils<K, V>{

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Resource
    private RedisTemplate<K, V> redisTemplate;

    public boolean setIfAbsent(K key, V value) {
        Boolean result = null;
        try {
            result = redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Throwable e) {
            logger.error("redisTemplate setIfAbsent error key={" + key + "}", e);
            throw e;
        } finally {
        }

        return result;
    }


    /**
     * 保存缓存，返回旧值
     *
     * @return
     */
    public Object getSet(final String key, final Serializable value) {
        Object result = redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {

                RedisSerializer serializer = getRedisSerializer();
                byte[] name = serializer.serialize(key);
                byte[] obj = serializer.serialize(value);
                byte[] val = connection.getSet(name, obj);
                if (val == null) {
                    return null;
                }

                return serializer.deserialize(val);
            }

        });

        return result;
    }

    public void setEx(final String key, final Serializable value, final long timeout) {
        redisTemplate.execute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {

                RedisSerializer serializer = getRedisSerializer();
                byte[] name = serializer.serialize(key);
                byte[] obj = serializer.serialize(value);
                connection.setEx(name, timeout,obj);
                return true;
            }

        });
    }

    public Long getExpire(final K key) {
        return redisTemplate.execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {

                RedisSerializer serializer = getRedisSerializer();

                byte[] name = serializer.serialize(key);

                return connection.ttl(name);
            }

        });
    }


    /**
     * 获取缓存
     *
     * @return
     */
    public Object get(final String key) {

        Object result = redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {

                RedisSerializer serializer = getRedisSerializer();
                byte[] name = serializer.serialize(key);
                byte[] value = connection.get(name);

                if (value == null) {
                    return null;
                }

                return serializer.deserialize(value);
            }

        });

        return result;
    }

    public boolean expire(K key, long timeout) {
        Boolean result = null;
        try {
            result = redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        } catch (Throwable e) {
            logger.error("redisTemplate setIfAbsent error key={" + key + "}", e);
            throw e;
        } finally {
        }

        return result;
    }

    public boolean delete(K key) {
        try {
            redisTemplate.delete(key);
        } catch (Throwable e) {
            logger.error("redisTemplate delete error key={" + key + "}", e);
            throw e;
        }

        return true;
    }

    /**
     * 保存缓存(如果key已经存在,不能重复保存)
     *
     * @return
     */
    public boolean putNX(final String key, final Serializable value) {

        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {

                RedisSerializer serializer = getRedisSerializer();
                byte[] name = serializer.serialize(key);
                byte[] obj = serializer.serialize(value);

                return connection.setNX(name, obj);
            }

        });

        return result;
    }

    /**
     * 保存缓存
     *
     * @return
     */
    public boolean put(final String key, final Serializable value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {

                RedisSerializer serializer = getRedisSerializer();
                byte[] name = serializer.serialize(key);
                byte[] obj = serializer.serialize(value);
                connection.set(name, obj);
                return true;
            }

        });

        return result;
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */

    public Map<Object, Object> hmget(K key) {
        Map<Object, Object> map=null;
        try {
            map=redisTemplate.opsForHash().entries(key);
        }catch (Throwable e){
            logger.error("redisTemplate hmget error key={" + key + "}", e);
            throw e;
        }
        return map;

    }
    /**
     * get方法，根据key和hashkey找出对应的值
     * @param key 键
     * @return 对应的value值
     */

    public String hget(K key,V value) {
        String str=null;
        try {
            str=redisTemplate.opsForHash().get(key, value).toString();
        }catch (Throwable e){
            logger.error("redisTemplate hget error key={" + key + "}", e);
            throw e;
        }
        return str;
    }

    public Long queuePush(K key, V value) {
        boolean flag = false;
        Long result = null;
        try {
            result = redisTemplate.opsForList().leftPush(key, value);
            flag = true;
        } catch (Throwable e) {
            logger.error("redisTemplate queuePush error key={" + key + "}", e);
            throw e;
        }

        return result;
    }

    public V queuePop(K key) {
        boolean flag = false;
        V result = null;
        try {
            result = redisTemplate.opsForList().rightPop(key);
            flag = true;
        } catch (Throwable e) {
            logger.error("redisTemplate queuePop error key={" + key + "}", e);
            throw e;
        }
        return result;
    }

    public Long queuePushHead(K key, V value) {
        boolean flag = false;
        Long result = null;
        try {
            result = redisTemplate.opsForList().rightPush(key, value);
            flag = true;
        } catch (Throwable e) {
            logger.error("redisTemplate queuePushHead error key={" + key + "}", e);
            throw e;
        }

        return result;
    }

    public V queueGet(K key, int index) {
        V result = null;
        try {
            result = redisTemplate.opsForList().index(key, index);
        } catch (Throwable e) {
            logger.error("redisTemplate queueGet error key={" + key + "}", e);
            throw e;
        }

        return result;
    }

    public Long queueRemove(K key, V value) {
        boolean flag = false;
        long result = 0;
        try {
            result = redisTemplate.opsForList().remove(key, 1, value);
            flag = true;
        } catch (Throwable e) {
            logger.error("redisTemplate queueRemove error key={" + key + "}", e);
            throw e;
        }

        return result;
    }

    public Long queueSize(K key) {
        long result = 0;
        try {
            result = redisTemplate.opsForList().size(key);
        } catch (Throwable e) {
            logger.error("redisTemplate queueRemove error key={" + key + "}", e);
            throw e;
        }

        return result;
    }

    /**
     * 获取 RedisSerializer
     */
    private RedisSerializer<?> getRedisSerializer() {
        return redisTemplate.getKeySerializer();
    }

    /**
     * 模糊删除Key
     * @param prex
     */
    public boolean deleteByPrex(K prex) {
        try{
            Set<K> keys = redisTemplate.keys(prex);
            if (CollectionUtils.isNotEmpty(keys)) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e){
            logger.error("redisTemplate deleteByPrex error key={" + prex + "}", e);
            throw e;
        }
        return true;
    }


    /**
     * 计数器加1
     * @param key
     * @return
     */
    public  Long increment(K key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long increment(K key, Long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
}
