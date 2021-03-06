package com.pay.aile.bill.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.pay.aile.bill.contant.BankKeywordContants;
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

    @Override
    public String initKeywords() {
        List<CreditBank> list = creditBankMapper.selectList(null);
        StringBuffer bankKeywords = new StringBuffer(256);
        for (CreditBank creditBank : list) {
            // bankKeywords.append(creditBank.getName());
            // bankKeywords.append(BankKeywordContants.BANK_KEYWORD_SEPARATOR);
            if (!StringUtils.isEmpty(creditBank.getExtKeyword())) {
                bankKeywords.append(creditBank.getExtKeyword());
                bankKeywords.append(BankKeywordContants.BANK_KEYWORD_SEPARATOR);
            }
        }

        BankKeywordContants.ALL_BANK_KEYWORDS = bankKeywords.deleteCharAt(bankKeywords.length() - 1).toString();
        logger.info("BankKeywordContants.ALL_BANK_KEYWORDS:{}", BankKeywordContants.ALL_BANK_KEYWORDS);
        return BankKeywordContants.ALL_BANK_KEYWORDS;
    }
    
    /**
     * 获取关键字列表
     * @return
     */
    public List<String> getKeywords() {
        List<CreditBank> list = creditBankMapper.selectList(null);
        List<String> result = new ArrayList<String>();
        for (CreditBank creditBank : list) {
            // bankKeywords.append(creditBank.getName());
            // bankKeywords.append(BankKeywordContants.BANK_KEYWORD_SEPARATOR);
            if (!StringUtils.isEmpty(creditBank.getExtKeyword())) {
            	result.add(creditBank.getExtKeyword());
            }
        }

        return result;
    }
    
    
    @Override
    public String initKeywordsReg() {
        List<CreditBank> list = creditBankMapper.selectList(null);
        StringBuffer bankKeywords = new StringBuffer(1024);
        for (CreditBank creditBank : list) {
            // bankKeywords.append(creditBank.getName());
            // bankKeywords.append(BankKeywordContants.BANK_KEYWORD_SEPARATOR);
            if (!StringUtils.isEmpty(creditBank.getExtKeyword())) {

                String[] kyeArray = creditBank.getExtKeyword().split(BankKeywordContants.BANK_KEYWORD_SEPARATOR);
                for (String key : kyeArray) {
                    bankKeywords.append("(\\S*\\s*").append(key).append("\\S*\\s*)|");
                }
            }
        }

        BankKeywordContants.ALL_BANK_KEYWORDS_REG = bankKeywords.deleteCharAt(bankKeywords.length() - 1).toString();
        logger.info("BankKeywordContants.ALL_BANK_KEYWORDS:{}", BankKeywordContants.ALL_BANK_KEYWORDS);
        return BankKeywordContants.ALL_BANK_KEYWORDS_REG;
    }
}
