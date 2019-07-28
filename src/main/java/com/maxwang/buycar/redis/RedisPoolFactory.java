package com.maxwang.buycar.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private JedisConfig jedisConfig;

    /**
     * 注入bean
     *
     * @return
     */
    @Bean
    public JedisPool jedisPoolFactory() {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(jedisConfig.getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(jedisConfig.getMaxWait()*1000);
        jedisPoolConfig.setMaxTotal(jedisConfig.getMaxActive());

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout() , redisConfig.getPassword(), 0);
        return jedisPool;
    }
}
