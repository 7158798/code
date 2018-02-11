package com.pay.aile.bill.service;

import java.util.List;

import com.pay.aile.bill.entity.CreditBill;

/**
 *
 * @author Charlie
 * @description
 */
public interface CreditBillService {

    public List<CreditBill> getBillList(CreditBill bill);

    public Long saveCreditBill(CreditBill bill);

    public Long saveOrUpdateCreditBill(CreditBill bill);
}
