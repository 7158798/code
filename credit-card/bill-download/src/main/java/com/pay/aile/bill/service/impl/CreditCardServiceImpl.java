package com.pay.aile.bill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.mapper.CreditCardMapper;
import com.pay.aile.bill.service.CreditCardService;

@Service
public class CreditCardServiceImpl implements CreditCardService {
	@Autowired
	private CreditCardMapper creditCardMapper;

	/**
	 *
	 * @Title: findCreditCard
	 * @Description:查询信用卡
	 * @param card
	 * @return CreditCard 返回类型 @throws
	 */
	@Override
	public CreditCard findCreditCard(CreditCard card) {
		return creditCardMapper.selectOne(card);
	}

	/**
	 *
	 * @Title: saveOrUpateCreditCard
	 * @Description: 保存
	 * @param card
	 * @return Long 返回类型 @throws
	 */
	@Override
	public Long saveOrUpateCreditCard(CreditCard card) {
		// 根据卡号查询
		CreditCard oldCard = new CreditCard();
		oldCard.setNumbers(card.getNumbers());
		oldCard = creditCardMapper.selectOne(card);
		if (oldCard != null) {
			card.setId(oldCard.getId());
		}
		try {
			if (card.getId() != null) {
				creditCardMapper.updateById(card);
			} else {
				creditCardMapper.insert(card);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return card.getId();
	}
}
