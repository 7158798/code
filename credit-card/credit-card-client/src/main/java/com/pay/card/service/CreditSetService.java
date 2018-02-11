package com.pay.card.service;

import java.util.List;
import java.util.Map;

import com.pay.card.model.CreditSet;

public interface CreditSetService {

    public CreditSet findCreditSet(CreditSet creditSet) throws Exception;

    public List<Map<String, Object>> findCreditSetList(CreditSet creditSet, Integer page) throws Exception;

    public CreditSet saveCreditSet(CreditSet creditSet);

    public CreditSet saveOrUpdate(CreditSet creditSet) throws Exception;
}
