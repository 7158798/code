package com.pay.aile.bill.analyze.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.AbstractBankMailAnalyzer;
import com.pay.aile.bill.enums.BankCodeEnum;
import com.pay.aile.bill.analyze.banktemplate.gdb.AbstractGDBTemplate;

/**
 *
 * @author Charlie
 * @description 广发银行
 */
@Service
public class GDBAnalyzer extends AbstractBankMailAnalyzer<AbstractGDBTemplate> {

    @Override
    public boolean support(String name) {
        return StringUtils.hasText(name) && (name.equalsIgnoreCase(BankCodeEnum.GDB.getBankCode())
                || name.contains(BankCodeEnum.GDB.getKeywords()));
    }

}
