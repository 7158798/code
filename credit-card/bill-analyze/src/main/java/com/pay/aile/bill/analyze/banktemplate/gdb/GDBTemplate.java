package com.pay.aile.bill.analyze.banktemplate.gdb;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankSeparateShareDetailTemplate;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.DateUtil;
import com.pay.aile.bill.utils.PatternMatcherUtil;

/**
 *
 * @author Charlie
 * @description 广发银行解析模板
 */
@Service
public class GDBTemplate extends BaseBankSeparateShareDetailTemplate implements AbstractGDBTemplate {

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(4L);
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
            rules.setYearMonth("\\d{4}年\\d{2}月的信用卡电子账单");
            rules.setCycle("账单周期： *\\d{4}/\\d{2}/\\d{2}至\\d{4}/\\d{2}/\\d{2}");
            rules.setCardNumbers("\\d{4}\\*+\\d{4}");
            rules.setCurrentAmount("\\d{4}\\*+\\d{4} (存款余额)?\\d+\\.?\\d*");
            rules.setMinimum("\\d{4}\\*+\\d{4} \\d+\\.?\\d* \\d+\\.?\\d*");
            rules.setDueDate("\\d{4}\\*+\\d{4} \\d+\\.?\\d* \\d+\\.?\\d* \\d{4}/\\d{2}/\\d{2}");
            rules.setCredits("\\d{4}\\*+\\d{4} \\d+\\.?\\d* \\d+\\.?\\d* \\d{4}/\\d{2}/\\d{2} \\S+ \\d+\\.?\\d*");
        }
    }

    @Override
    protected void analyzeCurrentAmount(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCurrentAmount())) {
            List<String> currentAmountList = getValueListByPattern("currentAmount", content, rules.getCurrentAmount(),
                    defaultSplitSign);
            currentAmountList.stream().map(amount -> {
                if (amount.contains("存款余额")) {
                    return amount.replaceAll("存款余额-?\\d+.?\\d*", "0");
                } else {
                    return amount;
                }
            }).collect(Collectors.toList());
            currentAmountList = PatternMatcherUtil.getMatcher(Constant.pattern_amount, currentAmountList);
            if (!currentAmountList.isEmpty()) {
                currentAmountList.stream().map(item -> {
                    if (item.startsWith("-")) {
                        return item.replaceAll("-", "");
                    } else {
                        return item;
                    }
                }).forEach(currentAmount -> {
                    CreditBill bill = new CreditBill();
                    bill.setCurrentAmount(new BigDecimal(currentAmount));
                    billList.add(bill);
                });
            }
        }
    }

    @Override
    protected void analyzeCycle(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCycle())) {

            String cycle = getValueByPattern("cycle", content, rules.getCycle(), apm, "：");
            String[] sa = cycle.split("至");
            billList.forEach(bill -> {
                bill.setBeginDate(DateUtil.parseDate(sa[0]));
                bill.setEndDate(DateUtil.parseDate(sa[1]));
            });
        }
    }

    @Override
    protected void analyzeYearMonth(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getYearMonth())) {
            String yearMonth = getValueByPattern("yearMonth", content, rules.getYearMonth(), apm, "");
            if (StringUtils.hasText(yearMonth)) {
                String year = yearMonth.substring(0, 4);
                String month = yearMonth.substring(5, 7);
                billList.forEach(bill -> {
                    bill.setYear(year);
                    bill.setMonth(month);
                });
            }
        }
        billList.forEach(bill -> {
            if (!StringUtils.hasText(bill.getYear()) || !StringUtils.hasText(bill.getMonth())) {
                if (bill.getEndDate() != null) {
                    bill.setYear(DateUtil.getDateField(bill.getEndDate(), Calendar.YEAR));
                    bill.setMonth(DateUtil.getDateField(bill.getEndDate(), Calendar.MONTH));
                }
            }
        });
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.GDB_DEFAULT;
    }

}
