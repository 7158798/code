package com.pay.card.service;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.pay.card.BaseTest;
import com.pay.card.model.CreditUserBillRelation;

public class CreditUserBillRelationServiceImplTest extends BaseTest {

    // public static void main(String[] args) {
    // System.out.println(new Date());
    // }

    @Autowired
    private CreditUserBillRelationService creditUserBillRelationService;

    @Test
    public void findTest() {
        CreditUserBillRelation creditUserBillRelation = new CreditUserBillRelation();
        creditUserBillRelation.setUserId(2l);

        // List<CreditUserBillRelation> list = creditUserBillRelationService
        // .findCreditUserBillRelation(creditUserBillRelation);

        // System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void saveTest() {
        CreditUserBillRelation creditUserBillRelation = new CreditUserBillRelation();
        creditUserBillRelation.setBillId(1l);
        creditUserBillRelation.setUserId(2l);
        creditUserBillRelation.setCreateDate(new Date());
        creditUserBillRelation.setStatus(1);
        System.out.println(JSON.toJSONString(creditUserBillRelation));

        try {
            creditUserBillRelationService.saveCreditUserBillRelation(creditUserBillRelation);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
