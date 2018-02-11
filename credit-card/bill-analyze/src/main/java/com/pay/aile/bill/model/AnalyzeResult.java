package com.pay.aile.bill.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditCard;

/**
 *
 * @author Charlie
 * @description 存储解析结果
 */
public class AnalyzeResult implements Serializable {
    /**
     * @author chao.wang
     */
    private static final long serialVersionUID = -4475559028026750766L;
    /**
     * 账单
     */
    private List<CreditBill> billList;
    /**
     * 行用卡
     */
    private List<CreditCard> cardList;

    /**
     * 账单明细
     */
    private List<CreditBillDetail> detailList;

    public AnalyzeResult() {
        billList = new ArrayList<CreditBill>();
        detailList = new ArrayList<CreditBillDetail>();
        cardList = new ArrayList<CreditCard>();
    }

    @Override
    public String toString() {
        return "AnalyzeResult [billList=" + billList + ", cardList=" + cardList + ", detailList=" + detailList + "]";
    }

    public List<CreditBill> getBillList() {
        return billList;
    }

    public void setBillList(List<CreditBill> billList) {
        this.billList = billList;
    }

    public List<CreditCard> getCardList() {
        return cardList;
    }

    public void setCardList(List<CreditCard> cardList) {
        this.cardList = cardList;
    }

    public List<CreditBillDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<CreditBillDetail> detailList) {
        this.detailList = detailList;
    }

}
