package com.pay.card.service;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.pay.card.BaseTest;
import com.pay.card.dao.CreditUserCardRelationDao;
import com.pay.card.model.CreditUserCardRelation;

public class CreditUserCardRelationServiceImplTest extends BaseTest {

    @Autowired
    private CreditUserCardRelationDao creditUserCardRelationDao;
    @Autowired
    private CreditUserCardRelationService creditUserCardRelationService;

    @Test
    public void findOneTest() {
        CreditUserCardRelation creditUserCardRelation = creditUserCardRelationDao.findCreditUserCardRelationOne(1l, 2880l);

        System.out.println(creditUserCardRelation);
    }

    @Test
    public void findTest() {
        CreditUserCardRelation creditUserCardRelation = new CreditUserCardRelation();
        creditUserCardRelation.setUserId(1l);
        List<CreditUserCardRelation> list = creditUserCardRelationService.findCreditUserCardRelation(creditUserCardRelation);

        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void saveTest() {
        CreditUserCardRelation creditUserCardRelation = new CreditUserCardRelation();
        creditUserCardRelation.setCardId(1l);
        creditUserCardRelation.setStatus(1);
        creditUserCardRelation.setUserId(1l);
        creditUserCardRelation.setCreateDate(new Date());
        // creditUserCardRelationService.saveCreditUserCardRelation(creditUserCardRelation);

    }
}
