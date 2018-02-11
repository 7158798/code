package com.pay.aile.bill.analyze.banktemplate.ccb;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankSeparateShareDetailTemplate;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.mapper.CreditTemplateMapper;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.DateUtil;

/**
 *
 * @author Charlie
 * @description 建设银行解析模板
 */
@Service
public class CCBTemplateNew extends BaseBankSeparateShareDetailTemplate implements AbstractCCBTemplate {
    private static Logger logger = LoggerFactory.getLogger(CCBTemplateNew.class);

    @Autowired
    CreditTemplateMapper creditTemplateMapper;

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(16L);
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
            rules.setCycle("StatementCycle \\d{4}/\\d{2}/\\d{2}-\\d{4}/\\d{2}/\\d{2}");
            rules.setBillDay("StatementDate \\d{4}-\\d{2}-\\d{2}");
            rules.setDueDate("PaymentDueDate \\d{4}/\\d{2}/\\d{2}");
            rules.setCredits("CreditLimit [a-zA-Z]+\\d+\\.?\\d*");
            rules.setCash("CashAdvanceLimit [a-zA-Z]+\\d+\\.?\\d*");
            rules.setCardNumbers("\\d+\\*{4}\\d{4} 人民币");
            rules.setCurrentAmount("\\d+\\*{4}\\d{4} 人民币\\(CNY\\) -?\\d+\\.?\\d*");
            rules.setMinimum("\\d+\\*{4}\\d{4} 人民币\\(CNY\\) -?\\d+\\.?\\d* -?\\d+\\.?\\d*");
            rules.setDetails(
                    "\\d{4}-\\d{2}-\\d{2} \\d{4}-\\d{2}-\\d{2} \\d{4}/?(\\d{4})? \\S+ [A-Za-z]+ -?\\d+\\.?\\d* [A-Za-z]+ -?\\d+\\.?\\d*");
            rules.setTransactionDate("0");
            rules.setBillingDate("1");
            rules.setTransactionDescription("3");
            rules.setTransactionCurrency("4");
            rules.setTransactionAmount("5");
            rules.setAccountableAmount("7");
        }
    }

    @Override
    protected void extractBillContent(AnalyzeParamsModel apm) {
        String content = extractor.extract(apm.getOriginContent(), "font");
        apm.setContent(content);

    }

    @Override
    protected LocalDate getThisDueDate(CreditCard card, CreditBill bill, AnalyzeParamsModel apm) {
        if (!StringUtils.hasText(card.getBillDay()) || bill.getDueDate() == null || card.getEndDate() == null) {
            return LocalDate.now();
        }
        LocalDate endDate = DateUtil.dateToLocalDate(card.getEndDate());
        int billDay = Integer.parseInt(card.getBillDay());
        LocalDate dueDate = DateUtil.dateToLocalDate(bill.getDueDate());
        // 1~8号是本月
        if (billDay <= 8) {
            return LocalDate.of(endDate.getYear(), endDate.getMonth(), dueDate.getDayOfMonth());
        } else if (billDay == 9 || billDay == 10) {
            if (endDate.getMonthValue() == 2) {
                return LocalDate.of(endDate.getYear(), endDate.getMonthValue() + 1, 1);
            } else {
                return LocalDate.of(endDate.getYear(), endDate.getMonthValue(), dueDate.getDayOfMonth());
            }
        } else {
            logger.info(" dueDate.getDayOfMonth()===================={}", dueDate.getDayOfMonth());
            return LocalDate.of(endDate.getYear(), endDate.getMonthValue(), dueDate.getDayOfMonth()).plusMonths(1);
        }

    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.CCB_LK_NEW;
    }
}
