package com.pay.aile.bill.utils;

import java.util.Arrays;

/**
 *
 * @Description: 基于redis实现的分布式锁
 * @see: RedisLoclUtil 此处填写需要参考的类
 * @version 2017年12月11日 上午10:28:09
 * @author chao.wang
 */
public class RedisLock {
    /**
     * 锁过期时间
     */
    private static final int defaultExpireTime = 5;

    public static boolean getLock(String key, String requestId, int expireTime) {
        if (expireTime <= 0) {
            expireTime = defaultExpireTime;
        }
        // 1:key,2:requestId,3:expireTime
        String script = "local success = redis.call('setnx',KEYS[1],KEYS[2]) if success == 1 then redis.call('setex',KEYS[1],KEYS[3],KEYS[2]) return 'true' else return 'false' end";
        String[] keys = new String[] { key, requestId, String.valueOf(expireTime) };
        String success = JedisClusterUtils.executeEval(script, Arrays.asList(keys));
        return new Boolean(success);
    }

    public static boolean releaseLock(String key, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then redis.call('del', KEYS[1]) return 'true' else return 'false' end";
        String result = JedisClusterUtils.executeEval(script, Arrays.asList(key), requestId);
        return new Boolean(result);
    }
}
