/**
 *
 */
package com.pay.card.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.pay.card.enums.RedisStatusEnum;
import com.pay.card.utils.ApiHelper;
import com.pay.card.web.context.RedisRequestContext;


/**
 * @author qiaohui
 * @param <T>
 */
@Controller
public abstract class BaseController<T> {

    protected final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    protected ApiHelper apiHelper;

    public void redisCache(Object obj, RedisStatusEnum status, String delKey) {
        if (RedisStatusEnum.QUERY.getCode().equals(status.getCode())) {
            try {
                //RedisRequestContext.setRequestContext("cacheValue", JsonUtils.toJsonString(obj));
            } catch (Exception e) {
                logger.error("json转换异常:{}", e);
            }
            RedisRequestContext.setRequestContext("redisStatus", status.getCode());
        } else {
            RedisRequestContext.setRequestContext("redisStatus", status.getCode());
            RedisRequestContext.setRequestContext("redisDelKey", delKey);
        }
    }

}
