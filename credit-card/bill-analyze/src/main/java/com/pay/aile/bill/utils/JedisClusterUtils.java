package com.pay.aile.bill.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

@Component
public class JedisClusterUtils {

    // private static JedisClusterUtils cacheUtils;

    private static RedisTemplate<String, String> redisTemplate;

    public static void delete(Set<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 从hashmap中删除一个值
     *
     * @param key
     *            map名
     * @param field
     *            成员名称
     */
    public static void delFromMap(String key, String field) {

        redisTemplate.execute((RedisCallback<Long>) connection -> connection.hDel(key.getBytes(), field.getBytes()));

    }

    /**
     * 从sorted set删除一个值
     *
     * @param key
     *            set名
     * @param member
     *            成员名称
     */
    public static void delFromSortedset(String key, String member) {
        redisTemplate.execute((RedisCallback<Long>) connection -> connection.zRem(key.getBytes(), member.getBytes()));

    }

    /**
     * 从缓存中删除数据
     *
     * @param string
     * @return
     */
    public static void delKey(String key) {

        redisTemplate.execute((RedisCallback<Long>) connection -> connection.del(key.getBytes()));
    }

    /**
     *
     * @Description
     * @param script
     * @param resultType
     * @param keys
     * @param args
     * @return
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static String executeEval(String script, List<String> keys, Object... args) {
        RedisScript<String> redisScript = new DefaultRedisScript<String>(script, String.class);
        return redisTemplate.execute(redisScript, keys, args);
    }

    /**
     * 设置超时时间
     *
     * @param key
     * @param seconds
     */
    public static void expire(String key, int seconds) {
        redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.expire(key.getBytes(), seconds));

    }

    /**
     * 取得复杂类型数据
     *
     * @param key
     * @param obj
     * @param clazz
     * @return
     */
    public static <T> T getBean(String key, Class<T> clazz) {

        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return JSON.parseObject(value, clazz);
    }

    /**
     * 从hash map中取得复杂类型数据
     *
     * @param key
     * @param field
     * @param clazz
     */
    public static <T> T getBeanFromMap(String key, String field, Class<T> clazz) {

        byte[] input = redisTemplate.execute((RedisCallback<byte[]>) connection -> {
            return connection.hGet(key.getBytes(), field.getBytes());
        });
        return JSON.parseObject(input, clazz, Feature.AutoCloseSource);
    }

    /**
     *
     * @Description: 根据key获取当前计数结果
     * @author clg
     * @date 2016年6月30日 下午2:38:20
     *
     * @param key
     * @return
     */
    public static String getCount(String key) {

        return redisTemplate.opsForValue().get(key);
    }

    /**
     *
     * 功能: 从指定队列里取得数据<br />
     * 作者: 耿建委
     *
     * @param key
     * @param size
     *            数据长度
     * @return
     */
    public static List<String> getFromQueue(String key, long size) {

        boolean flag = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            return connection.exists(key.getBytes());
        });

        if (flag) {
            return new ArrayList<>();
        }
        ListOperations<String, String> lo = redisTemplate.opsForList();
        if (size > 0) {
            return lo.range(key, 0, size - 1);
        } else {
            return lo.range(key, 0, lo.size(key) - 1);
        }
    }

    /**
     *
     *
     * @param key
     *            缓存Key
     * @return keyValue
     * @author:mijp
     * @since:2017/1/16 13:23
     */
    public static String getFromSet(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 根据成员名取得sorted sort分数
     *
     * @param key
     *            set名
     * @param member
     *            成员名
     * @return 分数
     */
    public static Double getMemberScore(String key, String member) {

        return redisTemplate.execute((RedisCallback<Double>) connection -> {
            return connection.zScore(key.getBytes(), member.getBytes());
        });
    }

    /**
     * 逆序取得sorted sort排名
     *
     * @param key
     *            set名
     * @param member
     *            成员名
     * @return 排名
     */
    public static Long getRankRev(String key, String member) {

        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            return connection.zRevRank(key.getBytes(), member.getBytes());
        });

    }

    /**
     * 将序列值回退一个
     *
     * @param key
     * @return
     */
    public static void getSeqBack(String key) {

        redisTemplate.execute((RedisCallback<Long>) connection -> connection.decr(key.getBytes()));

    }

    /**
     * 取得序列值的下一个
     *
     * @param key
     * @return
     */
    public static Long getSeqNext(String key) {

        return redisTemplate.execute((RedisCallback<Long>) connection -> {

            return connection.incr(key.getBytes());

        });
    }

    /**
     * 取得序列值的下一个
     *
     * @param key
     * @return
     */
    public static Long getSeqNext(String key, long value) {

        return redisTemplate.execute((RedisCallback<Long>) connection -> {

            return connection.incrBy(key.getBytes(), value);

        });

    }

    /**
     * 从缓存中取得字符串数据
     *
     * @param key
     * @return 数据
     */
    public static String getString(String key) {
        redisTemplate.opsForValue().get(key);

        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 判断hash集合中是否缓存了数据
     *
     * @param hName
     * @param key
     *            数据KEY
     * @return 判断是否缓存了
     */
    public static boolean hashCached(String hName, String key) {

        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            return connection.hExists(key.getBytes(), key.getBytes());
        });
    }

    /**
     * 从hash集合里取得
     *
     * @param hName
     * @param key
     * @return
     */
    public static Object hashGet(String hName, String key) {

        return redisTemplate.opsForHash().get(hName, key);
    }

    public static <T> T hashGet(String hName, String key, Class<T> clazz) {

        return JSON.parseObject((String) hashGet(hName, key), clazz);
    }

    /**
     *
     * @Description
     * @param hName
     * @param key
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static void hashIncr(String hName, String key) {
        redisTemplate.opsForHash().increment(hName, key, 1);
    }

    /**
     * 保存到hash集合中
     *
     * @param hName
     *            集合名
     * @param key
     * @param val
     */
    public static void hashSet(String hName, String key, String value) {

        redisTemplate.opsForHash().put(hName, key, value);
    }

    /**
     * 保存到hash集合中
     *
     * @param <T>
     *
     * @param hName
     *            集合名
     * @param key
     * @param val
     */
    public static <T> void hashSet(String hName, String key, T t) {

        hashSet(hName, key, JSON.toJSONString(t));
    }

    /**
     * 根据key获取所以值
     *
     * @param key
     * @return
     */
    public static Map<Object, Object> hgetAll(String key) {

        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 保存到hash集合中 只在 key 指定的哈希集中不存在指定的字段时，设置字段的值。如果 key 指定的哈希集不存在，会创建一个新的哈希集并与
     * key 关联。如果字段已存在，该操作无效果。
     *
     * @param hName
     *            集合名
     * @param key
     * @param val
     */
    public static void hsetnx(String hName, String key, String value) {

        redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.hSetNX(key.getBytes(), key.getBytes(),
                value.getBytes()));

    }

    /**
     * 保存到hash集合中 只在 key 指定的哈希集中不存在指定的字段时，设置字段的值。如果 key 指定的哈希集不存在，会创建一个新的哈希集并与
     * key 关联。如果字段已存在，该操作无效果。
     *
     * @param <T>
     *
     * @param hName
     *            集合名
     * @param key
     * @param val
     */
    public static <T> void hsetnx(String hName, String key, T t) {
        hsetnx(hName, key, JSON.toJSONString(t));
    }

    /**
     *
     * @Description: 根据key增长 ，计数器
     * @author clg
     * @date 2016年6月30日 下午2:37:52
     *
     * @param key
     * @return
     */
    public static long incr(String key) {

        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            return connection.incr(key.getBytes());
        });
    }

    public static long incrBy(String key, long delta) {

        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            return connection.incrBy(key.getBytes(), delta);
        });
    }

    /**
     * 增加浮点数的值
     *
     * @param key
     * @return
     */
    public static Double incrFloat(String key, double incrBy) {

        return redisTemplate.execute((RedisCallback<Double>) connection -> {

            return connection.incrBy(key.getBytes(), incrBy);

        });
    }

    /**
     * 判断是否缓存了数据
     *
     * @param key
     *            数据KEY
     * @return 判断是否缓存了
     */
    public static boolean isCached(String key) {

        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            return connection.exists(key.getBytes());
        });
    }

    /**
     * 判断是否缓存在指定的集合中
     *
     * @param key
     *            数据KEY
     * @param val
     *            数据
     * @return 判断是否缓存了
     */
    public static boolean isMember(String key, String val) {

        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            return connection.sIsMember(key.getBytes(), val.getBytes());
        });
    }

    /**
     * 列出set中所有成员
     *
     * @param setName
     *            set名
     * @return
     */
    public static Set<Object> listSet(String setName) {

        return redisTemplate.opsForHash().keys(setName);

    }

    /**
     * 逆序列出sorted set包括分数的set列表
     *
     * @param key
     *            set名
     * @param start
     *            开始位置
     * @param end
     *            结束位置
     * @return 列表
     */
    public static Set<Tuple> listSortedsetRev(String key, int start, int end) {

        return redisTemplate.execute((RedisCallback<Set<Tuple>>) connection -> {
            return connection.zRevRangeWithScores(key.getBytes(), start, end);
        });
    }

    /**
     * 返回存储在 key 里的list的长度。 如果 key 不存在，那么就被看作是空list，并且返回长度为 0
     *
     * @param key
     * @return
     */
    public static Long llen(String key) {

        return redisTemplate.opsForList().size(key);
    }

    /**
     * 移除并且返回 key 对应的 list 的第一个元素
     *
     * @param key
     * @return
     */
    public static String lpop(String key) {

        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 将所有指定的值插入到存于 key 的列表的头部。如果 key 不存在，那么在进行 push 操作前会创建一个空列表
     *
     * @param <T>
     *
     * @param key
     * @param value
     * @return
     */
    public static <T> Long lpush(String key, T value) {

        return redisTemplate.opsForList().leftPush(key, JSON.toJSONString(value));
    }

    /**
     * 只有当 key 已经存在并且存着一个 list 的时候，在这个 key 下面的 list 的头部插入 value。 与 LPUSH 相反，当
     * key 不存在的时候不会进行任何操作
     *
     * @param key
     * @param value
     * @return
     */
    public static <T> Long lpushx(String key, T value) {

        return redisTemplate.opsForList().leftPushIfPresent(key, JSON.toJSONString(value));
    }

    /**
     * 返回存储在 key 的列表里指定范围内的元素。 start 和 end
     * 偏移量都是基于0的下标，即list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推
     *
     * @param key
     * @return
     */
    public static List<String> lrange(String key, long start, long end) {

        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     *
     * 功能: 从指定队列里取得数据<br />
     * 作者: 耿建委
     *
     * @param key
     * @return
     */
    public static String popQueue(String key) {

        return redisTemplate.opsForList().rightPop(key);

    }

    /**
     * 保存复杂类型数据到缓存
     *
     * @param key
     * @param obj
     * @return
     */
    public static void saveBean(String key, Object obj) {

        redisTemplate.opsForValue().set(key, JSON.toJSONString(obj));
    }

    /**
     * 保存复杂类型数据到缓存（并设置失效时间）
     *
     * @param key
     * @param Object
     * @param seconds
     * @return
     */
    public static void saveBean(String key, Object obj, int seconds) {

        redisTemplate.opsForValue().set(key, JSON.toJSONString(obj), seconds, TimeUnit.SECONDS);
    }

    /**
     * 将递增浮点数存入缓存
     */
    public static void saveFloat(String key, float data) {

        redisTemplate.delete(key);
        redisTemplate.opsForValue().increment(key, data);
    }

    /**
     * 将 key的值保存为 value ，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。 SETNX 是『SET
     * if Not eXists』(如果不存在，则 SET)的简写。 <br>
     * 保存成功，返回 true <br>
     * 保存失败，返回 false
     */
    public static boolean saveNX(String key, String val) {

        /** 设置成功，返回 1 设置失败，返回 0 **/
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            return connection.setNX(key.getBytes(), val.getBytes());
        });

    }

    /**
     * 将 key的值保存为 value ，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。 SETNX 是『SET
     * if Not eXists』(如果不存在，则 SET)的简写。 <br>
     * 保存成功，返回 true <br>
     * 保存失败，返回 false
     *
     * @param key
     * @param val
     * @param expire
     *            超时时间
     * @return 保存成功，返回 true 否则返回 false
     */
    public static boolean saveNX(String key, String val, int expire) {

        boolean ret = saveNX(key, val);
        if (ret) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return ret;
    }

    /**
     * 将自增变量存入缓存
     */
    public static void saveSeq(String key, long seqNo) {

        redisTemplate.delete(key);
        redisTemplate.opsForValue().increment(key, seqNo);
    }

    /**
     * 将数据存入缓存
     *
     * @param key
     * @param val
     * @return
     */
    public static void saveString(String key, String val) {

        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        vo.set(key, val);
    }

    /**
     * 将数据存入缓存（并设置失效时间）
     *
     * @param key
     * @param val
     * @param seconds
     * @return
     */
    public static void saveString(String key, String val, long seconds) {

        redisTemplate.opsForValue().set(key, val, seconds, TimeUnit.SECONDS);
    }

    /**
     * 功能: 存到指定的队列中<br />
     * 左近右出<br\> 作者: 耿建委
     *
     * @param key
     * @param val
     * @param size
     *            队列大小限制 0：不限制
     */
    public static void saveToQueue(String key, String val, long size) {

        ListOperations<String, String> lo = redisTemplate.opsForList();

        if (size > 0 && lo.size(key) >= size) {
            lo.rightPop(key);
        }
        lo.leftPush(key, val);
    }

    /**
     * 将数据存入缓存的集合中
     *
     * @param key
     * @param val
     * @return
     */
    public static void saveToSet(String key, String val) {

        SetOperations<String, String> so = redisTemplate.opsForSet();

        so.add(key, val);
    }

    /**
     * 向sorted set中追加一个值
     *
     * @param key
     *            set名
     * @param score
     *            分数
     * @param member
     *            成员名称
     */
    public static void saveToSortedset(String key, Double score, String member) {

        redisTemplate.execute(
                (RedisCallback<Boolean>) connection -> connection.zAdd(key.getBytes(), score, member.getBytes()));
    }

    public static Set<String> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 向set中追加一个值
     *
     * @param setName
     *            set名
     * @param value
     */
    public static void setSave(String setName, String value) {

        redisTemplate
                .execute((RedisCallback<Long>) connection -> connection.sAdd(setName.getBytes(), value.getBytes()));
    }

    @Resource(name = "redisTemplate")
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        if (JedisClusterUtils.redisTemplate == null) {
            JedisClusterUtils.redisTemplate = redisTemplate;
        }
    }

    // @PostConstruct
    // public void init() {
    // cacheUtils = this;
    // redisTemplate = redisTemplate;
    // }
}
