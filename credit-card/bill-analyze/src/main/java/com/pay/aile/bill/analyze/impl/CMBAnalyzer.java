package com.pay.aile.bill.analyze.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.AbstractBankMailAnalyzer;
import com.pay.aile.bill.analyze.banktemplate.cmb.AbstractCMBTemplate;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.enums.BankCodeEnum;
import com.pay.aile.bill.mapper.CreditFileMapper;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.JedisClusterUtils;

/**
 *
 * @author Charlie
 * @description 招商银行解析模板
 */
@Service
public class CMBAnalyzer extends AbstractBankMailAnalyzer<AbstractCMBTemplate> {

    /**
     * credit-cmb-simple-not-support-${email}-${userid}
     */
    private static final String CREDIT_CMB_SIMPLE_NOT_SUPPORT = "credit-cmb-simple-not-support-%s-%s";
    @Autowired
    private CreditFileMapper creditFileMapper;

    @Override
    public boolean support(String name) {
        return StringUtils.hasText(name) && (name.equalsIgnoreCase(BankCodeEnum.CMB.getBankCode())
                || name.contains(BankCodeEnum.CMB.getKeywords()));
    }

    @Override
    protected void applyAnalyzeError(AnalyzeParamsModel apm) {
        if (apm != null) {
            // 招商银行简版账单判断
            if ("招商银行信用卡电子账单".equals(apm.getSubject())) {
                JedisClusterUtils.saveString(
                        String.format(CREDIT_CMB_SIMPLE_NOT_SUPPORT, apm.getEmail(), apm.getUserId()),
                        "cmb simple not support");
                CreditFile file = new CreditFile();

                file.setCmbSimple(1);
                file.setId(apm.getFileId());
                creditFileMapper.updateById(file);
            }

        }
    }

}
