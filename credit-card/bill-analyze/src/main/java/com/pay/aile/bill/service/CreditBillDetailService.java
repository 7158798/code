package com.pay.aile.bill.service;

import java.util.List;

import com.pay.aile.bill.entity.CreditBillDetail;

/**
 *
 * @author Charlie
 * @description
 */
public interface CreditBillDetailService {
    public void batchSaveBillDetail(int year, int month, List<CreditBillDetail> detailList);

    public Long saveCreditBillDetail(CreditBillDetail billDetail);

}
