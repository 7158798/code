package com.pay.aile.bill.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.mapper.CreditBillMapper;
import com.pay.aile.bill.service.CreditBillService;

/**
 *
 * @author Charlie
 * @description
 */
@Service
public class CreditBillServiceImpl implements CreditBillService {

    @Autowired
    private CreditBillMapper creditBillMapper;

    @Override
    public List<CreditBill> getBillList(CreditBill bill) {
        Wrapper<CreditBill> wrapper = new EntityWrapper<CreditBill>();
        return creditBillMapper.selectList(wrapper);
    }

    @Transactional
    @Override
    public Long saveCreditBill(CreditBill bill) {
        return creditBillMapper.insertCreditBill(bill);
    }

    @Transactional
    @Override
    public Long saveOrUpdateCreditBill(CreditBill bill) {
        if (bill.getId() == null) {
            creditBillMapper.insertCreditBill(bill);
        } else {
            creditBillMapper.updateById(bill);
        }
        return bill.getId();
    }

}
