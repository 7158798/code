package com.pay.aile.bill.analyze.banktemplate.czb;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankTemplate;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.PatternMatcherUtil;

/**
 *
 * @author Charlie
 * @description 浙商银行解析模板
 */
@Service
public class CZBTemplate extends BaseBankTemplate implements AbstractCZBTemplate {

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(2L);
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
            rules.setCredits("PaymentDueDate \\d+\\.?\\d*");
            rules.setMinimum("PaymentDueDate \\d+\\.?\\d* \\d+\\.?\\d*");
            rules.setBillDay("PaymentDueDate \\d+\\.?\\d* \\d+\\.?\\d* \\d{8}");
            rules.setYearMonth("PaymentDueDate \\d+\\.?\\d* \\d+\\.?\\d* \\d{8}");
            rules.setDueDate("PaymentDueDate \\d+\\.?\\d* \\d+\\.?\\d* \\d{8} \\d{8}");
            rules.setCurrentAmount("循环利息Interest \\d+\\.?\\d*");
            rules.setDetails("\\d{8} \\d{8} \\S+ -?\\d+\\.?\\d* \\d{4}");
            rules.setIntegral("Interestthismonth \\d+");
            rules.setTransactionDate("0");
            rules.setBillingDate("1");
            rules.setTransactionDescription("2");
            rules.setTransactionAmount("3");
            rules.setCardNumbers("4");
        }
    }

    @Override
    protected void analyzeYearMonth(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getYearMonth())) {
            String yearMonth = getValueByPattern("yearMonth", content, rules.getYearMonth(), apm, defaultSplitSign);
            yearMonth = StringUtils.collectionToDelimitedString(PatternMatcherUtil.getMatcher("\\d+", yearMonth), "");
            if (StringUtils.hasText(yearMonth)) {
                String year = yearMonth.substring(0, 4);
                String month = yearMonth.substring(4, 6);
                billList.forEach(bill -> {
                    bill.setYear(year);
                    bill.setMonth(month);
                });
            }
        }
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.CZB_DEFAULT;
    }

}
