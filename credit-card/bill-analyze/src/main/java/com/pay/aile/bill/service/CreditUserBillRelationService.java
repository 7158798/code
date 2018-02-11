package com.pay.aile.bill.service;

import java.util.List;

import com.pay.aile.bill.entity.CreditUserBillRelation;

public interface CreditUserBillRelationService {
    void batchSave(List<CreditUserBillRelation> creditUserBillRelationList);
}
