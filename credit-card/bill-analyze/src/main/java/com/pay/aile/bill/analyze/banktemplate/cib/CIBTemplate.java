package com.pay.aile.bill.analyze.banktemplate.cib;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankSeparateShareDetailTemplate;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.DateUtil;
import com.pay.aile.bill.utils.PatternMatcherUtil;

/**
 *
 * @author Charlie
 * @description 兴业银行解析模板
 */
@Service
public class CIBTemplate extends BaseBankSeparateShareDetailTemplate implements AbstractCIBTemplate {

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(8L);
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+您好");
            rules.setYearMonth("\\d{4}年\\d{2}月");
            rules.setCycle("StatementCycle\\d{4}/\\d{2}/\\d{2}-\\d{4}/\\d{2}/\\d{2}");
            rules.setDueDate("PaymentDueDate\\d{4}年\\d{2}月\\d{2}日");
            rules.setCredits("CreditLimit\\(RMB\\)\\d+\\.?\\d*");
            rules.setCash("CashAdvanceLimit\\(RMB}\\)\\d+\\.?\\d*");

            rules.setCurrentAmount("本期应缴余额:\\d+\\.?\\d*");
            rules.setMinimum("本期最低还款额:\\d+\\.?\\d*");
            rules.setIntegral("BonusPointBalance\\d+");

            rules.setCardNumbers("卡号末四位\\d{4}");
            rules.setDetails(
                    "\\d{4}-\\d{2}-\\d{2}(\\d{2}:\\d{2})? \\d{4}-\\d{2}-\\d{2} \\S+ ?(-?\\d+\\.?\\d*[A-Z-a-z]*)? -?\\d+\\.\\d*");
            rules.setTransactionDate("0");
            rules.setBillingDate("1");
            rules.setTransactionDescription("2");
            rules.setTransactionAmount("3");
        }
    }

    @Override
    protected void analyzeCash(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCash())) {
            List<String> cash = getValueListByPattern("cash", content, rules.getCash(),
                    "CashAdvanceLimit\\([a-zA-Z]{3}\\)");
            cash = PatternMatcherUtil.getMatcher(Constant.pattern_amount, cash);
            if (!cash.isEmpty()) {
                for (int i = 0; i < cash.size(); i++) {
                    CreditBill bill = billList.get(i);
                    bill.setCash(new BigDecimal(cash.get(i)));
                }
            }
        }

    }

    @Override
    protected void analyzeCredits(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCredits())) {
            List<String> credits = getValueListByPattern("credits", content, rules.getCredits(),
                    "CreditLimit\\([a-zA-Z]{3}\\)");
            credits = PatternMatcherUtil.getMatcher(Constant.pattern_amount, credits);
            if (!credits.isEmpty()) {
                for (int i = 0; i < credits.size(); i++) {
                    CreditBill bill = billList.get(i);
                    bill.setCredits(new BigDecimal(credits.get(i)));
                }
            }
        }
    }

    @Override
    protected void analyzeCurrentAmount(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCurrentAmount())) {
            List<String> currentAmountList = getValueListByPattern("currentAmount", content, rules.getCurrentAmount(),
                    "NewBalance[a-zA-Z]{3}");
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

            String cycle = getValueByPattern("cycle", content, rules.getCycle(), apm, "");
            cycle = cycle.replaceAll("StatementCycle", "");
            String[] sa = cycle.split("-");
            billList.forEach(bill -> {
                bill.setBeginDate(DateUtil.parseDate(sa[0]));
                bill.setEndDate(DateUtil.parseDate(sa[1]));
            });
        }
    }

    @Override
    protected void analyzeDueDate(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getDueDate())) {
            String date = getValueByPattern("dueDate", content, rules.getDueDate(), apm, "PaymentDueDate");
            billList.forEach(bill -> {
                bill.setDueDate(DateUtil.parseDate(date));
            });
        }
    }

    @Override
    protected void analyzeMinimum(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getMinimum())) {
            List<String> minimumList = getValueListByPattern("minimum", content, rules.getMinimum(), "");
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
                    CreditBill bill = billList.get(i);
                    bill.setMinimum(new BigDecimal(minimumList.get(i)));
                }
            }
        }

    }

    @Override
    protected void analyzeYearMonth(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getYearMonth())) {
            String yearMonth = getValueByPattern("yearMonth", content, rules.getYearMonth(), apm, "");
            yearMonth = yearMonth.replaceAll("年|月|-|/|账单", "");
            yearMonth = PatternMatcherUtil.getMatcherString("\\d{6}", yearMonth);
            if (StringUtils.hasText(yearMonth)) {
                String year = yearMonth.substring(0, 4);
                String month = yearMonth.substring(4);
                billList.forEach(bill -> {
                    bill.setYear(year);
                    bill.setMonth(month);
                });
            }
        }
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.CIB_DEFAULT;
    }

    @Override
    protected CreditBillDetail setCreditBillDetail(String detail) {
        CreditBillDetail cbd = new CreditBillDetail();
        String[] sa = detail.split(" ");
        if (sa[0].length() > 10) {
            sa[0] = sa[0].substring(0, 10);
        }
        cbd.setTransactionDate(DateUtil.parseDate(sa[0]));
        cbd.setBillingDate(DateUtil.parseDate(sa[1]));
        cbd.setTransactionDescription(sa[2]);
        if (sa.length == 4) {
            cbd.setTransactionAmount(sa[3]);
        } else if (sa.length == 5) {
            String amount = PatternMatcherUtil.getMatcherString(Constant.pattern_amount, sa[3]);
            String currency = PatternMatcherUtil.getMatcherString("[a-zA-Z]*", sa[3]);
            cbd.setTransactionAmount(amount);
            cbd.setTransactionCurrency(currency);
            cbd.setAccountableAmount(sa[4]);
        }
        return cbd;

    }

}
