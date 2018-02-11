package com.pay.aile.bill.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnProperty(name = "redis.sentinel")
public class RedisSentinelConfig {
    private Logger logger = LoggerFactory.getLogger(RedisSentinelConfig.class);
    @Value("${redis.host}")
    private String host;
    @Value("${redis.password}")
    private String password;
    @Value("${redis.masterName}")
    private String masterName;
    @Value("${redis.dbIndex}")
    private Integer dbIndex;
    private int timeout = 4000;

    @Bean
    public JedisConnectionFactory initJedisConnectionFactory() {
        Set<String> sentinels = new HashSet<String>();
        sentinels.addAll(Arrays.asList(host.split(";")));
        RedisSentinelConfiguration rscfg = new RedisSentinelConfiguration(masterName, sentinels);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(rscfg, initJedisPoolConfig());
        jedisConnectionFactory.setDatabase(dbIndex);
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setTimeout(timeout);
        return jedisConnectionFactory;
    }

    @Bean
    public JedisPoolConfig initJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(500);
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setMaxIdle(200);
        jedisPoolConfig.setBlockWhenExhausted(true);
        jedisPoolConfig.setMaxWaitMillis(3000);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(false);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        jedisPoolConfig.setNumTestsPerEvictionRun(-1);
        jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(-1);
        jedisPoolConfig.setLifo(false);
        return jedisPoolConfig;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {

        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(initJedisConnectionFactory());

        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        @SuppressWarnings("rawtypes")
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);

        logger.info("######################create RedisTemplate success");
        return redisTemplate;
    }
}
