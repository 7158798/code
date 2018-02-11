package com.pay.card.view;

import com.pay.card.enums.RepaymentTypeEnum;
import com.pay.card.model.CreditRepayment;
import com.pay.card.utils.AmountUtil;
import com.pay.card.utils.DateUtil;
import com.pay.card.web.context.CardBuildContext;

public class CreditRePayMentView {

    private final CreditRepayment creditRepayment;

    private final CardBuildContext buildContext;

    public CreditRePayMentView(CreditRepayment repayment, CardBuildContext buildContext) {
        creditRepayment = repayment;
        this.buildContext = buildContext;
    }

    /**
     * @Title: getAmount
     * @Description: 还款金额
     * @param
     * @return BigDecimal
     */
    public String getAmount() {
        if (creditRepayment.getAmount() != null) {
            return AmountUtil.amountFormat(creditRepayment.getAmount());

        }
        return null;
    }

    /**
     * @Title: getCreateDate
     * @Description: 还款时间
     * @param
     * @return String
     */
    public String getCreateDate() {
        return DateUtil.formatDate(creditRepayment.getCreateDate());
    }

    /**
     * @Title: getType
     * @Description: 还款类型
     * @param
     * @return String
     */
    public String getType() {
        if (RepaymentTypeEnum.MARK_REPAYMENT.getCode().equals(creditRepayment.getType())) {
            return RepaymentTypeEnum.MARK_REPAYMENT.getMsg();
        } else if (RepaymentTypeEnum.MARK_PAIDOFF.getCode().equals(creditRepayment.getType())) {
            return RepaymentTypeEnum.MARK_PAIDOFF.getMsg();
        } else if (RepaymentTypeEnum.BILL_REPAYMENT.getCode().equals(creditRepayment.getType())) {
            return RepaymentTypeEnum.BILL_REPAYMENT.getMsg();
        } else if (RepaymentTypeEnum.PRACTICAL_REPAYMENT.getCode().equals(creditRepayment.getType())) {
            return RepaymentTypeEnum.PRACTICAL_REPAYMENT.getMsg();
        }
        return null;
    }

}
