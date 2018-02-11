package com.pay.card.redis;

import java.util.Set;

import org.junit.Test;

import com.pay.card.BaseTest;
import com.pay.card.utils.JedisUtil;

public class RedisTest extends BaseTest {

    @Test
    public void redisDelTest() {
        Set<String> keys = JedisUtil.getKeys("credit_key_" + "*");
        JedisUtil.delete(keys);
    }

    @Test
    public void redisTest() {
        JedisUtil.saveString("credit_key_" + "billid", "123");
        JedisUtil.saveString("credit_key_" + "cardid", "456");

        Set<String> keys = JedisUtil.getKeys("credit_key_" + "*");
        System.out.println(keys);

    }

}
