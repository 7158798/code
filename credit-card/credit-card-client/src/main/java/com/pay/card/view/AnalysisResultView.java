package com.pay.card.view;

import java.time.LocalDate;

import org.springframework.util.StringUtils;

import com.pay.card.model.CreditCard;
import com.pay.card.utils.DateUtil;
import com.pay.card.web.context.CardBuildContext;

public class AnalysisResultView {

    private final CreditCard creditCard;

    private final CardBuildContext buildContext;

    public AnalysisResultView(CreditCard card, CardBuildContext buildContext) {
        creditCard = card;
        this.buildContext = buildContext;
    }

    /*
     * 银行名称
     */
    public String getBankCode() {
        if (creditCard.getBank() != null) {
            return creditCard.getBank().getCode();
        }
        return "";
    }

    /*
     * 银行名称
     */
    public String getBankName() {
        if (creditCard.getBank() != null) {
            return creditCard.getBank().getName();
        }
        return "";
    }

    /*
     * 账单数量
     */
    public Integer getBillCount() {
        return creditCard.getBillSize();
    }

    /*
     * 持卡人
     */
    public String getCardholder() {
        if (StringUtils.hasText(creditCard.getCardholder())) {
            return creditCard.getCardholder();
        }
        return "";
    }

    /*
     * id
     */
    public Long getId() {
        return creditCard.getId();
    }

    /*
     * 0 旧的 1 新的
     */
    public Integer getIsNew() {

        if (creditCard.getCreateDate() != null) {
            LocalDate localCreditCard = DateUtil.dateToLocalDate(creditCard.getCreateDate());
            if (localCreditCard.equals(LocalDate.now())) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    /*
     * 卡号
     */
    public String getNumbers() {
        String numbers = creditCard.getNumbers();
        if (numbers != null && numbers.length() > 4) {
            numbers = numbers.substring(numbers.length() - 4, numbers.length());
            return numbers;
        }
        return numbers;
    }
}
