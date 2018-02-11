package com.pay.card.service;

import java.util.List;

import com.pay.card.model.CreditEmail;
import com.pay.card.model.CreditUserEmailRelation;

public interface CreditUserEmailRelationService {

    public List<CreditUserEmailRelation> findCreditUserEmailRelation(CreditUserEmailRelation creditUserEmailRelation) throws Exception;

    public List<CreditEmail> findEmailByUser(Long userId) throws Exception;

    public void saveCreditUserEmailRelation(CreditUserEmailRelation creditUserEmailRelation) throws Exception;

    public void unbindEmail(Long userId, Long emailId) throws Exception;
}
