package com.pay.aile.bill.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditBillDetailRelation;
import com.pay.aile.bill.mapper.CreditBillDetailRelationMapper;
import com.pay.aile.bill.service.CreditBillDetailRelationService;

/**
 *
 * @author Charlie
 * @description
 */
@Service
public class CreditBillDetailRelationServiceImpl implements CreditBillDetailRelationService {

    @Autowired
    private CreditBillDetailRelationMapper creditBillDetailRelationMapper;

    @Transactional
    @Override
    public void batchSaveBillDetailRelation(int year, int month, List<CreditBillDetailRelation> relationList) {
    	if(!relationList.isEmpty()) {
    		 creditBillDetailRelationMapper.batchInsert(year, month, relationList);
    	}
       
    }

}
