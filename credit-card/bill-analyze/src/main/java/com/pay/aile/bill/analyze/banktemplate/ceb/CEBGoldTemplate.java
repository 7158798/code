package com.pay.aile.bill.analyze.banktemplate.ceb;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankSeparateStringTemplate;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.DateUtil;

/**
 *
 * @author Charlie
 * @description 光大银行信用卡账单内容解析模板
 */
@Service
public class CEBGoldTemplate extends BaseBankSeparateStringTemplate implements AbstractCEBTemplate {

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(12L);
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
            rules.setCycle("\\d{4}年\\d{2}月\\d{2}日至\\d{4}年\\d{2}月\\d{2}日");
            rules.setYearMonth("中国光大银行信用卡电子账单\\(\\d{4}年\\d{2}月\\)");
            // 账单日
            rules.setBillDay("积分余额RewardsPointsBalance \\d{4}/\\d{2}/\\d{2}");
            // 到期还款日
            rules.setDueDate("积分余额RewardsPointsBalance \\d{4}/\\d{2}/\\d{2} \\d{4}/\\d{2}/\\d{2}");
            // 额度
            rules.setCredits("积分余额RewardsPointsBalance \\d{4}/\\d{2}/\\d{2} \\d{4}/\\d{2}/\\d{2} \\d+\\.?\\d*");
            // 应还款额
            rules.setCurrentAmount("\\d+\\*{4}\\d+ \\S+ \\d+\\.?\\d* \\d+\\.?\\d*");
            rules.setMinimum("\\d+\\*{4}\\d+ \\S+ \\d+\\.?\\d* \\d+\\.?\\d* \\d+\\.?\\d*");
            rules.setDetails("\\d{4}/\\d{2}/\\d{2} \\d{4}/\\d{2}/\\d{2} \\d{4} \\S+ -?\\d+\\.?\\d*");
            rules.setCardNumbers("\\d+\\*{4}\\d+");
            rules.setIntegral("PointsexpiringonDecember31st \\d+");
        }
    }

    @Override
    protected void analyzeCardNo(List<CreditCard> cardList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCardNumbers())) {
            Exception error = null;
            try {
                Integer.valueOf(rules.getCardNumbers());
            } catch (Exception e) {
                error = e;
            }
            if (error != null) {
                List<String> cardNos = getValueListByPattern("cardNumbers", content, rules.getCardNumbers(),
                        defaultSplitSign);
                if (!cardNos.isEmpty()) {
                    for (int i = 0; i < cardNos.size(); i++) {
                        String cardNo = cardNos.get(i);
                        if (StringUtils.hasText(cardNo)) {
                            cardNo = cardNo.replaceAll("-", "");
                        }
                        CreditCard card = new CreditCard();
                        card.setNumbers(cardNo);
                        if (!cardList.contains(card)) {
                            cardList.add(card);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void analyzeCycle(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCycle())) {
            String cycle = getValueByPattern("cycle", content, rules.getCycle(), apm, defaultSplitSign);
            String[] sa = cycle.split("至");
            billList.forEach(bill -> {
                bill.setBeginDate(DateUtil.parseDate(sa[0]));
                bill.setEndDate(DateUtil.parseDate(sa[1]));
            });
        }
    }

    @Override
    protected int indexOfCardDetail(String text, String cardNo) {
        return text.indexOf("账号AccountNumber：" + cardNo);
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.CEB_GOLD;
    }

    @Override
    protected CreditBillDetail setCreditBillDetail(String detail) {
        detail = detail.replaceAll("\\n", "");
        CreditBillDetail cbd = new CreditBillDetail();
        String[] sa = detail.split(" ");
        cbd.setTransactionDate(DateUtil.parseDate(sa[0]));
        cbd.setBillingDate(DateUtil.parseDate(sa[1]));
        cbd.setTransactionAmount(sa[sa.length - 1]);
        String desc = "";
        for (int i = 3; i < sa.length - 1; i++) {
            desc = desc + sa[i];
        }
        cbd.setTransactionDescription(desc);
        return cbd;
    }

}
