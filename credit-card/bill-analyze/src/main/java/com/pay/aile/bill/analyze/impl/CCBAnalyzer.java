package com.pay.aile.bill.analyze.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.AbstractBankMailAnalyzer;
import com.pay.aile.bill.enums.BankCodeEnum;
import com.pay.aile.bill.analyze.banktemplate.ccb.AbstractCCBTemplate;

/**
 *
 * @author Charlie
 * @description 建设银行
 */
@Service("CCBAnalyzer")
public class CCBAnalyzer extends AbstractBankMailAnalyzer<AbstractCCBTemplate> {

    @Override
    public boolean support(String name) {
        return StringUtils.hasText(name) && (name.equalsIgnoreCase(BankCodeEnum.CCB.getBankCode())
                || name.contains(BankCodeEnum.CCB.getKeywords()));
    }

}
