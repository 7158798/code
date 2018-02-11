package com.pay.aile.bill.service.impl;

import java.util.Date;
import java.util.List;

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
    public void batchSaveBillDetail(int year, int month, List<CreditBillDetail> detailList) {
        detailList.forEach(detail -> {
            detail.setCreateDate(new Date());
        });
        if (year > 0 && month > 0) {
            creditBillDetailMapper.batchInsert(year, month, detailList);
        } else {
            creditBillDetailMapper.batchInsertBase(detailList);
        }
    }

    @Transactional
    @Override
    public Long saveCreditBillDetail(CreditBillDetail billDetail) {
        creditBillDetailMapper.insert(billDetail);
        return billDetail.getId();
    }

}
