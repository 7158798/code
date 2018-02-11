package com.pay.aile.bill.analyze.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.AbstractBankMailAnalyzer;
import com.pay.aile.bill.analyze.banktemplate.spdb.AbstractSPDBTemplate;
import com.pay.aile.bill.enums.BankCodeEnum;

/**
 *
 * @author Charlie
 * @description 浦发银行
 */
@Service
public class SPDBAnalyzer extends AbstractBankMailAnalyzer<AbstractSPDBTemplate> {

    @Override
    public boolean support(String name) {
        return StringUtils.hasText(name) && (name.equalsIgnoreCase(BankCodeEnum.SPDB.getBankCode())
                || name.contains(BankCodeEnum.SPDB.getKeywords()));
    }

}
