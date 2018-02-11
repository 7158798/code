package com.pay.aile.bill.analyze.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.AbstractBankMailAnalyzer;
import com.pay.aile.bill.enums.BankCodeEnum;
import com.pay.aile.bill.analyze.banktemplate.czb.AbstractCZBTemplate;

/**
 *
 * @author Charlie
 * @description 浙商银行
 */
@Service
public class CZBAnalyzer extends AbstractBankMailAnalyzer<AbstractCZBTemplate> {

    @Override
    public boolean support(String name) {
        return StringUtils.hasText(name) && (name.equalsIgnoreCase(BankCodeEnum.CZB.getBankCode())
                || name.contains(BankCodeEnum.CZB.getKeywords()));
    }

}
