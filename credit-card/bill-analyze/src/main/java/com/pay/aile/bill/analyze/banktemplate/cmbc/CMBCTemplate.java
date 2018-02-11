package com.pay.aile.bill.analyze.banktemplate.cmbc;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankTemplate;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.DateUtil;

/**
 *
 * @author zhibin.cui
 * @description 民生银行信用卡账单内容解析模板
 */
@Service
public class CMBCTemplate extends BaseBankTemplate implements AbstractCMBCTemplate {

    @Override
    public void initRules() {
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(13L);
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
            rules.setYearMonth("\\d{4}年\\d{2}月对账单");
            rules.setBillDay("StatementDate \\d{4}/\\d{2}/\\d{2}"); // 账单日
            rules.setDueDate("PaymentDueDate \\d{4}/\\d{2}/\\d{2}");
            rules.setCurrentAmount("NewBalance \\d+.?\\d*");
            rules.setMinimum("Min.Payment: 人民币 \\d+.?\\d*");
            rules.setIntegral("EndingBalance \\d+");
            rules.setCardNumbers("4");
            rules.setDetails("\\d{2}/\\d{2} \\d{2}/\\d{2} \\S+ -?\\d+\\.?\\d* \\d{4}");
        }
    }

    @Override
    protected void analyzeCycle(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getBillDay())) {
            String billDay = getValueByPattern("billDay", content, rules.getBillDay(), apm, " ");
            Date endDate = DateUtil.parseDate(billDay);
            billList.forEach(bill -> {
                bill.setBeginDate(
                        DateUtil.localDateToDate(DateUtil.dateToLocalDate(endDate).plusMonths(-1).plusDays(1)));
                bill.setEndDate(endDate);
            });
        }
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.CMBC_DEFAULT;
    }

    @Override
    protected CreditBillDetail setCreditBillDetail(String detail) {
        CreditBillDetail cbd = new CreditBillDetail();
        String[] sa = detail.split(" ");

        String transYear = DateUtil.getBillYearByMonth(sa[0].substring(0, 2));
        String billYear = DateUtil.getBillYearByMonth(sa[1].substring(0, 2));
        cbd.setTransactionDate(DateUtil.parseDate(transYear + "/" + sa[0]));
        cbd.setBillingDate(DateUtil.parseDate(billYear + "/" + sa[1]));
        cbd.setTransactionAmount(sa[sa.length - 2].replaceAll("\\n", ""));
        String desc = "";
        for (int i = 2; i < sa.length - 2; i++) {
            desc = desc + sa[i];
        }
        cbd.setTransactionDescription(desc);
        cbd.setCardNumbers(sa[sa.length - 1]);
        return cbd;
    }
}
