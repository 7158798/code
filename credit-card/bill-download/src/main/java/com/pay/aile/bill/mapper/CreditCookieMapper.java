package com.pay.aile.bill.mapper;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import com.pay.aile.bill.entity.CreditCookie;

public interface CreditCookieMapper  extends BaseMapper<CreditCookie>{
	 void batchInsert(List<CreditCookie> cookieList);
	 
	 void deleteByEmail(Long emailId);
}
