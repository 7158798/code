package com.pay.aile.bill.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditCookie;
import com.pay.aile.bill.entity.CreditNativeEmail;
import com.pay.aile.bill.mapper.CreditCookieMapper;
import com.pay.aile.bill.mapper.CreditNativeEmailMapper;
import com.pay.aile.bill.service.CreditNativeEmailService;

@Service
public class CreditNativeEmailServiceImpl implements CreditNativeEmailService {
	@Autowired
	private CreditNativeEmailMapper creditNativeEmailMapper;
	
	@Autowired
	private CreditCookieMapper creditCookieMapper;
	
	public CreditNativeEmail findCreditNative(CreditNativeEmail email) {
		return creditNativeEmailMapper.selectOne(email);
		
		
	}
	
	
	@Transactional
	public void saveOrUpdate(CreditNativeEmail email,Map<String,String> cookieMap) {
	
		
		if(email.getId()!=null) {
			//更新郵箱
			creditNativeEmailMapper.updateById(email);
			//刪除
			creditCookieMapper.deleteByEmail(email.getId());
			
		}else {
			creditNativeEmailMapper.insert(email);
		}
		//插入
		 List<CreditCookie> cookieList= mapToCookie(cookieMap,email);
		 creditCookieMapper.batchInsert(cookieList);
	}
	private List<CreditCookie> mapToCookie(Map<String,String> cookieMap,CreditNativeEmail email) {
		List<CreditCookie> cookieList= new ArrayList<CreditCookie>();
		cookieMap.forEach((key,value)->{
			CreditCookie cookie = new CreditCookie();
			cookie.setEmailId(email.getId());
			cookie.setKey(key);
			cookie.setValue(value);
			
			cookieList.add(cookie);
		});
		return cookieList;
	}
	                      
	
}
