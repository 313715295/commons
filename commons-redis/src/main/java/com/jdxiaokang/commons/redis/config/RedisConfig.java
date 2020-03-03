package com.jdxiaokang.commons.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;


/**
 * redis config
 * 
 * @author tanyong
 * @version $Id: RedisConfig.java, v 0.1 2018年6月11日 下午2:14:16 ThinkPad Exp $
 */
@Component
public class RedisConfig {


	@Bean("redisTemplate")
	public RedisTemplate<String,Object> integerRedisTemplate(RedisConnectionFactory redisConnectionFactory){
		RedisTemplate<String,Object> template = new RedisTemplate<>(); //只能对字符串的键值操作
		template.setConnectionFactory(redisConnectionFactory);


		// 使用Jackson2JsonRedisSerialize 替换默认序列化(默认采用的是JDK序列化)
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);

		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		template.setKeySerializer(stringSerializer);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setHashKeySerializer(stringSerializer);
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();

		return template;
	}

}
