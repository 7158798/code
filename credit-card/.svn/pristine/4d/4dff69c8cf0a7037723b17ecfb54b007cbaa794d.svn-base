package com.pay.aile.bill.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.mapper.CreditTemplateMapper;
import com.pay.aile.bill.service.CreditTemplateService;

@Service
public class CreditTemplateServiceImpl implements CreditTemplateService {
	@Autowired
	private CreditTemplateMapper creditTemplateMapper;

	/**
	 *
	 * @Title: getList
	 * @Description: 查询模板列表
	 * @param creditTemplate
	 * @return 参数 @return List<CreditTemplate> 返回类型 @throws
	 */
	public List<CreditTemplate> getList(CreditTemplate creditTemplate) {
		Wrapper<CreditTemplate> wrapper = new EntityWrapper<CreditTemplate>();
		// TODO 增加查询条件
		List<CreditTemplate> list = creditTemplateMapper.selectList(wrapper);
		return list;
	}

	/**
	 *
	 * @Title: saveOrUpdate
	 * @Description: 保存模板
	 * @param creditTemplate
	 * @return 参数 @return CreditTemplate 返回类型 @throws
	 */
	public CreditTemplate saveOrUpdate(CreditTemplate creditTemplate) {
		if (creditTemplate.getId() != null) {
			creditTemplateMapper.updateById(creditTemplate);
		} else {
			creditTemplateMapper.insert(creditTemplate);
		}

		return creditTemplate;
	}
}
