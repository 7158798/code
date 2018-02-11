package com.pay.card.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.pay.card.model.CreditBill;
import com.pay.card.utils.AmountUtil;
import com.pay.card.utils.DateUtil;
import com.pay.card.web.context.CardBuildContext;

public class CreditBillView {

    private final CreditBill creditBill;

    private final CardBuildContext buildContext;

    public CreditBillView(CreditBill bill, CardBuildContext buildContext) {
        creditBill = bill;
        this.buildContext = buildContext;
    }

    public String getBillCycle() {
        // TODO 账单周期
        // String date = creditBill.getYear() + "-" + creditBill.getMonth() +
        // "-" + creditBill.getCard().getBillDay();
        // date = DateUtil.getBillCycle(date);
        if (creditBill.getBeginDate() == null || creditBill.getEndDate() == null) {
            return "";
        }
        String b = "";
        if (creditBill.getBeginDate() != null) {
            b = DateUtil.formatMMDD3(creditBill.getBeginDate());
        }
        String e = "";
        if (creditBill.getEndDate() != null) {
            e = DateUtil.formatMMDD3(creditBill.getEndDate());
        }
        return b + "-" + e;
    }

    /*
     * 账单日
     */
    public String getBillDate() {
        if (creditBill.getEndDate() != null) {
            LocalDate date = DateUtil.dateToLocalDate(creditBill.getEndDate());
            return date.format(DateTimeFormatter.ofPattern("MM月dd日"));
        }
        return "1";
    }

    // public List<Map<String, String>> getBillDetail() {
    // List<CreditBillDetail> detailList = creditBill.getDetailList();
    // List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    // if (detailList != null) {
    // detailList.forEach(detial -> {
    // Map<String, String> detalMap = new HashMap<String, String>();
    // // 交易日期
    // detalMap.put("billDate",
    // DateUtil.formatMMDD2(detial.getTransactionDate()));
    // // 交易描述
    // detalMap.put("description", detial.getTransactionDescription());
    // // 交易金额
    // detalMap.put("amount", detial.getTransactionAmount());
    // result.add(detalMap);
    // });
    // }
    // return result;
    // }

    /*
     * 本期账单金额
     */
    public String getCurrentAmount() {
        if (creditBill.getCurrentAmount() != null) {
            return AmountUtil.amountFormat(creditBill.getCurrentAmount());
        }
        return "-1";
    }

    /*
     * 还款日
     */
    public String getDueDate() {
        if (creditBill.getDueDate() != null) {
            return DateUtil.formatMMDD(creditBill.getDueDate());
        }
        return "1";
    }

    public Long getId() {
        return creditBill.getId();
    }

    /**
     * @Title: getIsPayOff
     * @Description: 账单是否还清 1: 还清,0:未还清
     * @param
     * @return
     */
    public String getIsPayOff() {
        if (creditBill.getCard().getRepayment().compareTo(creditBill.getCurrentAmount()) >= 0) {
            return "1";
        } else {
            return "0";
        }
    }

    // public List<Map<String, String>> getRepaymentDetail() {
    // List<CreditRepayment> repaymentList = creditBill.getRepaymentList();
    // List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    // if (repaymentList != null) {
    // repaymentList.forEach(detial -> {
    // Map<String, String> detalMap = new HashMap<String, String>();
    // // 交易日期
    // detalMap.put("createDate", DateUtil.formatMMDD2(detial.getCreateDate()));
    // // 还款描述
    // detalMap.put("type", RepaymentTypeEnum.getMsg(detial.getType()));
    // // 还款金额
    // detalMap.put("amount", AmountUtil.amountFormat(detial.getAmount()));
    // result.add(detalMap);
    // });
    // }
    // return result;
    // }

    /*
     * 账单月
     */
    public String getMonth() {
        return creditBill.getMonth();
    }

    public String getNextBillDays() {
        return "";
    }

    /*
     * 账单年
     */
    public String getYear() {
        return creditBill.getYear();
    }

}
