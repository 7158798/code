package com.pay.aile.bill.service;

import java.util.Map;

import com.pay.aile.bill.entity.CreditNativeEmail;

public interface CreditNativeEmailService {

	public void saveOrUpdate(CreditNativeEmail email,Map<String,String> cookieMap);
	
	public CreditNativeEmail findCreditNative(CreditNativeEmail email) ;
}
