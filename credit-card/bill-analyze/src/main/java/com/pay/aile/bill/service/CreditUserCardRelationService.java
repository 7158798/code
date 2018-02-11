package com.pay.aile.bill.service;

import java.util.List;

import com.pay.aile.bill.entity.CreditUserCardRelation;

public interface CreditUserCardRelationService {
    void batchSave(List<CreditUserCardRelation> creditUserCardRelationList);
}
