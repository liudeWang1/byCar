package com.maxwang.buycar.redis;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.redis.jedis.pool")
public class JedisConfig {
    private int maxWait ;
    private int maxIdle ;
    private int maxActive;
}
