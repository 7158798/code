package com.pay.aile.bill.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditUserBillRelation;
import com.pay.aile.bill.mapper.CreditUserBillRelationMapper;
import com.pay.aile.bill.service.CreditUserBillRelationService;

@Service
public class CreditUserBillRelationServiceImpl implements CreditUserBillRelationService {

    @Autowired
    private CreditUserBillRelationMapper creditUserBillRelationMapper;

    @Transactional
    @Override
    public void batchSave(List<CreditUserBillRelation> creditUserBillRelationList) {
    	if(!creditUserBillRelationList.isEmpty()) {
    		  creditUserBillRelationMapper.batchInsert(creditUserBillRelationList);
    	}
      
    }

}
