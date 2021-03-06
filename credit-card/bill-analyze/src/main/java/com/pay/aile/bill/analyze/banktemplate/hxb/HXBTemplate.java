package com.pay.aile.bill.analyze.banktemplate.hxb;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankTemplate;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.PatternMatcherUtil;

/**
 * @author Charlie
 * @description 华夏银行信用卡账单内容解析模板
 */
@Service
public class HXBTemplate extends BaseBankTemplate implements AbstractHXBTemplate {

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(9L);
            rules.setYearMonth("(（|\\()\\d{4}年\\d{2}月份(）|\\))");
            rules.setBillDay("账单日StatementDate: 每月\\d{2}日");
            rules.setDueDate("本期到期还款日PaymentDueDate: \\d{4}/\\d{2}/\\d{2}");
            rules.setCredits("CreditLimit: \\d+\\.?\\d*");
            rules.setCash("CashAdvanceLimit: \\d+\\.?\\d*");
            rules.setCurrentAmount("本期应还金额AmountPayable RMB:-?\\d+\\.?\\d*");
            rules.setMinimum("最低还款额MinimumPayment RMB:-?\\d+\\.?\\d*");
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
            rules.setIntegral("PointsRedeemedthismonth \\d+");
            rules.setDetails("\\d{4}/\\d{2}/\\d{2} \\d{4}/\\d{2}/\\d{2} \\S+ -?\\d+\\.?\\d* \\d{4}");
            rules.setTransactionDate("0");
            rules.setBillingDate("1");
            rules.setTransactionDescription("2");
            rules.setTransactionAmount("3");
            rules.setCardNumbers("4");
        }

    }

    @Override
    protected void analyzeBillDate(List<CreditCard> cardList, List<CreditBill> billList, String content,
            AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getBillDay())) {
            String billDay = getValueByPattern("billDay", content, rules.getBillDay(), apm, "");
            if (StringUtils.hasText(billDay)) {
                final String finalBillDay = PatternMatcherUtil.getMatcherString("\\d{2}", billDay);
                cardList.forEach(card -> {
                    card.setBillDay(finalBillDay);
                });
                billList.forEach(bill -> {
                    bill.setBillDay(finalBillDay);
                });
            }
        }
    }

    @Override
    protected void analyzeCurrentAmount(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCurrentAmount())) {
            List<String> currentAmountList = getValueListByPattern("currentAmount", content, rules.getCurrentAmount(),
                    ":");
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
    protected void analyzeMinimum(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getMinimum())) {
            List<String> minimumList = getValueListByPattern("minimum", content, rules.getMinimum(), ":");
            minimumList = PatternMatcherUtil.getMatcher(Constant.pattern_amount, minimumList);
            if (!minimumList.isEmpty()) {
                minimumList = minimumList.stream().map(item -> {
                    if (item.startsWith("-")) {
                        return item.replaceAll("-", "");
                    } else {
                        return item;
                    }
                }).collect(Collectors.toList());
                for (int i = 0; i < minimumList.size(); i++) {
                    CreditBill bill = null;
                    if (!billList.isEmpty()) {
                        bill = billList.get(i);
                    } else {
                        bill = new CreditBill();
                        billList.add(bill);
                    }
                    bill.setMinimum(new BigDecimal(minimumList.get(i)));
                }
            }
        }
    }

    @Override
    protected void analyzeYearMonth(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getYearMonth())) {

            String yearMonth = getValueByPattern("yearMonth", content, rules.getYearMonth(), apm, "");
            yearMonth = yearMonth.replaceAll("（|）|\\(|\\)", "");
            String year = yearMonth.substring(0, 4);
            String month = yearMonth.substring(5, 7);
            billList.forEach(bill -> {
                bill.setYear(year);
                bill.setMonth(month);
            });
        }
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.HXB_DEFAULT;
    }
}
