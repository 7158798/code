package com.pay.aile.bill.analyze.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.AbstractBankMailAnalyzer;
import com.pay.aile.bill.analyze.banktemplate.cib.AbstractCIBTemplate;
import com.pay.aile.bill.enums.BankCodeEnum;

/**
 *
 * @author Charlie
 * @description 兴业银行
 */
@Service
public class CIBAnalyzer extends AbstractBankMailAnalyzer<AbstractCIBTemplate> {

    @Override
    public boolean support(String name) {
        return StringUtils.hasText(name) && (name.equalsIgnoreCase(BankCodeEnum.CIB.getBankCode())
                || name.contains(BankCodeEnum.CIB.getKeywords()));
    }

}
