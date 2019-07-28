package com.maxwang.buycar.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

    private String host;
    private int port;
    private int timeout;
    private String password;

    /*@Value("${spring.redis.jedis.pool.max-wait}")
    private int maxWait ;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle ;
    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;*/

}
