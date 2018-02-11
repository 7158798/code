/**
 * @Title: ClearDataCache.java
 * @Package com.pay.card.event
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年12月14日
 * @version V1.0
 */

package com.pay.card.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Description: 删除card首页缓存
 * @see: ClearCardDataCacheEvent 此处填写需要参考的类
 * @version 2018年1月11日 下午1:39:48
 * @author zhibin.cui
 */

public class ClearCardDataCacheEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1021975280599631065L;

    /**
     * 创建一个新的实例 ClearDataCache.
     * @param source
     */

    public ClearCardDataCacheEvent(Object source) {
        super(source);

    }

}
