package com.pay.aile.bill.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.pay.aile.bill.entity.CreditBank;
import com.pay.aile.bill.mapper.CreditBankMapper;
import com.pay.aile.bill.service.CreditBankService;

@Service
public class CreditBankServiceImpl implements CreditBankService {
    private static final Logger logger = LoggerFactory.getLogger(CreditBankServiceImpl.class);
    @Autowired
    private CreditBankMapper creditBankMapper;

    @Override
    public List<CreditBank> getAllList(CreditBank bank) {
        Wrapper<CreditBank> wapper = new EntityWrapper<CreditBank>(bank);
        return creditBankMapper.selectList(wapper);
    }

}
