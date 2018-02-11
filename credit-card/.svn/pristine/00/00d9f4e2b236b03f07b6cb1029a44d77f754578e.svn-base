package com.pay.aile.bill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.mapper.CreditBillDetailMapper;
import com.pay.aile.bill.service.CreditBillDetailService;

/**
 * 
 * @author Charlie
 * @description
 */
@Service
public class CreditBillDetailServiceImpl implements CreditBillDetailService {

    @Autowired
    private CreditBillDetailMapper creditBillDetailMapper;

    @Transactional
    @Override
    public Long saveCreditBillDetail(CreditBillDetail billDetail) {
        Long billId = billDetail.getBillId();
        if (billId == null || billId == 0) {

        }
        return creditBillDetailMapper.insertCreditBillDetail(billDetail);
    }

}
