package com.pay.aile.bill.analyze.banktemplate.citic;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankSeparateStringTemplate;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.DateUtil;

/**
 *
 * @author Charlie
 * @description 中信银行解析模板
 */
@Service
public class CITICTemplate extends BaseBankSeparateStringTemplate implements AbstractCITICTemplate {

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(23L);
            rules.setCardholder(" 尊敬的[\\u4e00-\\u9fa5]+");
            rules.setYearMonth("\\d{4}年\\d{2}月账单已产生");
            rules.setCycle("\\d{4}年\\d{2}月\\d{2}日-\\d{4}年\\d{2}月\\d{2}日");
            rules.setDueDate("到期还款日：\\d{4}年\\d{2}月\\d{2}日");
            rules.setCurrentAmount(
                    "\\d{4}-\\d{2}\\*{2}-\\*{4}-\\d{4} RMB -?\\d+\\.?\\d* -?\\d+\\.?\\d* -?\\d+\\.?\\d* -?\\d+\\.?\\d*");
            rules.setMinimum(
                    "\\d{4}-\\d{2}\\*{2}-\\*{4}-\\d{4} RMB -?\\d+\\.?\\d* -?\\d+\\.?\\d* -?\\d+\\.?\\d* -?\\d+\\.?\\d* -?\\d+\\.?\\d*");
            rules.setCredits("(取现额度|预借现金额度) RMB \\d+\\.?\\d*");
            rules.setCash("(取现额度|预借现金额度) RMB \\d+\\.?\\d* RMB \\d+\\.?\\d*");
            rules.setCardNumbers("\\d{4}-\\d{2}\\*{2}-\\*{4}-\\d{4} RMB");
            rules.setDetails("\\d{8} \\d{8} \\d{0,4} \\S+ RMB -?\\d+\\.?\\d* RMB -?\\d+\\.?\\d*");
            rules.setIntegral("有效期提醒 \\S+ \\d+ \\d+ \\d+ \\d+ \\d+");
            rules.setTransactionDate("0");
            rules.setBillingDate("1");
            rules.setTransactionDescription("3");
            rules.setTransactionCurrency("4");
            rules.setTransactionAmount("5");
            rules.setAccountableAmount("7");
        }
    }

    @Override
    protected void analyzeCycle(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCycle())) {

            String cycle = getValueByPattern("cycle", content, rules.getCycle(), apm, "");
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
            String date = getValueByPattern("dueDate", content, rules.getDueDate(), apm, "：");
            billList.forEach(bill -> {
                bill.setDueDate(DateUtil.parseDate(date));
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
    }

    @Override
    protected int indexOfCardDetail(String content, String cardNo) {
        return content.indexOf("◆卡号:" + cardNo);
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.CITIC_STANDARD;
    }

}
