package com.pay.card.view;

import org.springframework.util.StringUtils;

import com.pay.card.model.CreditBillDetail;
import com.pay.card.utils.DateUtil;
import com.pay.card.web.context.CardBuildContext;

public class CreditBillDetailView {

    private final CreditBillDetail billDetail;

    private final CardBuildContext buildContext;

    public CreditBillDetailView(CreditBillDetail creditBillDetail, CardBuildContext buildContext) {
        billDetail = creditBillDetail;
        this.buildContext = buildContext;
    }

    /**
     * @Title: getTransactionAmount
     * @Description: 账单金额
     * @param
     * @return
     */
    public String getTransactionAmount() {
        if (StringUtils.hasText(billDetail.getTransactionAmount())) {
            return billDetail.getTransactionAmount();
        }
        return "";
    }

    /**
     * @Title: getTransactionDate
     * @Description: 交易日期
     * @param
     * @return
     */
    public String getTransactionDate() {
        if (billDetail.getTransactionDate() != null) {
            return DateUtil.formatMMDD2(billDetail.getTransactionDate());
        }
        return "";
    }

    /**
     * @Title: getTransactionDescription
     * @Description: 交易描述
     * @param
     * @return
     */
    public String getTransactionDescription() {
        if (StringUtils.hasLength(billDetail.getTransactionDescription())) {
            return billDetail.getTransactionDescription();
        }
        return "";
    }
}
