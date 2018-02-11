package com.pay.card.service.impl;

import java.util.Set;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pay.card.service.RequestInfoService;

/**
 * 异步记录api 请求信息，可存入 redis
 * 
 * @author qiaohui
 *
 */
@Service
@EnableAsync
public class RequestInfoServiceImpl implements RequestInfoService {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Override
    @Async
    public void asyncRun(String header, String url, String rawData) {
        try {
            JSONObject raw = JSON.parseObject(rawData);
            JSONObject data = JSON.parseObject(header);
            if (data == null) {
                data = new JSONObject();
            }
            data.put("url", url);
            data.put("raw_data", raw);
            data.put("create_time", System.currentTimeMillis());

            logger.info("aysncLog.requestHeader: %d %d", System.currentTimeMillis(),
                    data != null ? data.toString() : "data null");

            if (data != null) {
                data.clear();
            }
            if (raw != null) {
                raw.clear();
            }
        } catch (Throwable e) {
            logger.error("aysncLog.error:" + e.toString(), rawData, e);
        }
    }

    @Override
    public Set<String> getRequestHeader(long start, long end) {
        return null;
    }
}
