package com.pay.aile.bill.analyze.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.AbstractBankMailAnalyzer;
import com.pay.aile.bill.analyze.banktemplate.psbc.AbstractPSBCTemplate;
import com.pay.aile.bill.enums.BankCodeEnum;

/**
 *
 * @author zhibin.cui
 * @description 邮储银行解析模版
 */
@Service("PSBCAnalyzer")
public class PSBCAnalyzer extends AbstractBankMailAnalyzer<AbstractPSBCTemplate> {

    @Override
    public boolean support(String name) {
        return StringUtils.hasText(name) && (name.equalsIgnoreCase(BankCodeEnum.PSBC.getBankCode())
                || name.contains(BankCodeEnum.PSBC.getKeywords()));
    }

}
