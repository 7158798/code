package com.pay.aile.bill.event;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * @Description: 删除client查询缓存
 * @see: ClearCacheDataEvent 此处填写需要参考的类
 * @version 2018年1月12日 上午9:28:29 
 * @author zhibin.cui
 */
public class ClearCacheDataEvent extends ApplicationEvent {

	private static final long serialVersionUID = -6445819797635136517L;

	public ClearCacheDataEvent(Object source) {
		super(source);
	}

}
