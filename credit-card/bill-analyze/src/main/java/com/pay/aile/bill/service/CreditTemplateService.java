package com.pay.aile.bill.service;

import java.util.List;

import com.pay.aile.bill.entity.CreditTemplate;

public interface CreditTemplateService {
	public List<CreditTemplate> getList(CreditTemplate creditTemplate);

	public CreditTemplate saveOrUpdate(CreditTemplate creditTemplate);
}
