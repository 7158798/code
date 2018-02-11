package com.pay.aile.bill.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditUserCardRelation;
import com.pay.aile.bill.mapper.CreditUserCardRelationMapper;
import com.pay.aile.bill.service.CreditUserCardRelationService;

@Service
public class CreditUserCardRelationServiceImpl implements CreditUserCardRelationService {

    @Autowired
    private CreditUserCardRelationMapper creditUserCardRelationMapper;

    @Transactional
    @Override
    public void batchSave(List<CreditUserCardRelation> creditUserCardRelationList) {
        creditUserCardRelationMapper.batchInsert(creditUserCardRelationList);
    }

}
