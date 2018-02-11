package com.pay.card.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.pay.card.bean.CreditCardBean;
import com.pay.card.model.CreditBill;
import com.pay.card.model.CreditEmail;

public interface CreditBillService {

    public JSONArray findBillCalendar(CreditCardBean creditCard) throws Exception;

    public List<CreditBill> findCreditBillList(CreditBill creditBill) throws Exception;

    public List<CreditEmail> findEmailByUser(Long userId) throws Exception;

    public List<CreditBill> findNewCreditBillList(CreditBill creditBill);

    public CreditBill saveOrUpdate(CreditBill creditBill) throws Exception;

    public void updateBillStatusById(Long billId, Long userId) throws Exception;

    void updateNewStatus(List<Long> billId);

}
