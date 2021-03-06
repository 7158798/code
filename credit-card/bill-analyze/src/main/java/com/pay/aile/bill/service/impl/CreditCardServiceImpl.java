package com.pay.aile.bill.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.mapper.CreditCardMapper;
import com.pay.aile.bill.service.CreditCardService;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
        card.setCreateDate(new Date());
        creditCardMapper.insertOrUpdate(card);
        if(card.getId()==null) {
        	card = creditCardMapper.findByUnioue(card);
        }
        return card.getId();
    }

    @Transactional
    @Override
    public List<CreditCard> saveOrUpateCreditCard(List<CreditCard> cardList) {
        cardList.forEach(card -> {
            card.setCreateDate(new Date());
            creditCardMapper.insertOrUpdate(card);
            if(card.getId()==null) {
            	card = creditCardMapper.findByUnioue(card);
            }
        });
        return new ArrayList<CreditCard>(cardList);
    }
    
    

    /**
     * 多账户统一还款的账单类型,抓取到一个卡号之后,在这个卡号失效之前,一直沿用此卡号,其余卡号忽略不记录
     */
    @Transactional
    @Override
    public Long saveOrUpdateUnionCreditCard(CreditCard card) {
        card.setCreateDate(new Date());
        creditCardMapper.insertOrUpdate(card);
        if(card.getId()==null) {
        	card = creditCardMapper.findByUnioue(card);
        }
        return card.getId();
    }

}
