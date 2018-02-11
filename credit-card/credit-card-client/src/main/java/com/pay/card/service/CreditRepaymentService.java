package com.pay.card.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.model.CreditRepayment;

public interface CreditRepaymentService {
    public CreditRepayment delete(CreditRepayment creditRepayment);

    public List<JSONObject> findRePaymentDetail(CreditRepayment creditRepayment) throws Exception;

    public List<CreditRepayment> findRepaymentList(CreditRepayment creditRepayment) throws Exception;

    public CreditRepayment saveOrUpdate(CreditRepayment creditRepayment) throws Exception;

}
