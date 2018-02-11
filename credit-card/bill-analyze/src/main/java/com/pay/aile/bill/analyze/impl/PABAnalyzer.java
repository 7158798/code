package com.pay.aile.bill.analyze.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.AbstractBankMailAnalyzer;
import com.pay.aile.bill.enums.BankCodeEnum;
import com.pay.aile.bill.analyze.banktemplate.pab.AbstractPABTemplate;

/**
 *
 * @author Charlie
 * @description 平安银行
 */
@Service
public class PABAnalyzer extends AbstractBankMailAnalyzer<AbstractPABTemplate> {

    @Override
    public boolean support(String name) {
        return StringUtils.hasText(name) && (name.equalsIgnoreCase(BankCodeEnum.PAB.getBankCode())
                || name.contains(BankCodeEnum.PAB.getKeywords()));
    }

}
