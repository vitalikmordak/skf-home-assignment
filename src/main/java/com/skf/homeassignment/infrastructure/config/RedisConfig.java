package com.skf.homeassignment.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

	@Bean
	LettuceConnectionFactory lettuceConnectionFactory(@Value("${spring.redis.host:localhost}") String host,
													  @Value("${spring.redis.port:6379}") int port) {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public RedisTemplate<String, ?> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
		RedisTemplate<String, ?> template = new RedisTemplate<>();
		template.setConnectionFactory(lettuceConnectionFactory);
		template.afterPropertiesSet();
		return template;
	}
}
