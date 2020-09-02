package com.mmall.common;

import com.mmall.utility.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

/**
 * redisoon初始化类
 */
@Component
@Slf4j
public class RedissonManager {

    private static Config config = new Config();
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    private static Redisson redisson = null;

    static {
        init();
    }

    private static void init(){
        //这个版本redis不支持一致性算法，所以只用一个redis
        try {
            config.useSingleServer().setAddress(new StringBuilder().append(redis1Ip).append(":").append(redis1Port).toString());
            redisson = (Redisson) Redisson.create(config);
            log.info("redisson init finish");
        } catch (Exception e) {
            log.info("redisson init error");
        }
    }

    public Redisson getRedisson(){
        return redisson;
    }

}
