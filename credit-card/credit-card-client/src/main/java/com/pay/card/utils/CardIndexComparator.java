/**
 * @Title: CardIndexComparator.java
 * @Package com.pay.card.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年12月20日
 * @version V1.0
 */

package com.pay.card.utils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.card.view.CreditCardView;

/**
 * @ClassName: CardIndexComparator
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月20日
 */

public class CardIndexComparator implements Comparator<CreditCardView> {

    private static Logger logger = LoggerFactory.getLogger(CardIndexComparator.class);

    @Override
    public int compare(CreditCardView card1, CreditCardView card2) {

        // 先按照逾期排序，
        LocalDate nowDate = LocalDate.now();
        Date now = DateUtil.localDateToDate(nowDate);
        int billDay1 = card1.getBillDay();
        int billDay2 = card2.getBillDay();

        long d1 = DateUtil.getdifferenceDay(now, card1.getDueDate());
        long d2 = DateUtil.getdifferenceDay(now, card2.getDueDate());
        // logger.info("card1.Id:{},card2.Id:{}", card1.getId(), card2.getId());
        if (card1.getCardStatus() > card2.getCardStatus()) {
            return -1;
        } else if (card1.getCardStatus().equals(card2.getCardStatus())) {
            // 逾期
            if (card1.getCardStatus() != 0) {
                if (d1 >= d2) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                // 未还款
                if (billDay1 <= billDay2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        } else {
            return 1;
        }

        // int cardStatus1 = card1.getDay().get("cardStatus");
        // int cardStatus2 = card2.getDay().get("cardStatus");
        // int cardDay1 = card1.getDay().get("day");
        // int cardDay2 = card2.getDay().get("day");
        // if (cardStatus1 > cardStatus2) {
        // return -1;
        // } else if (cardStatus1 == cardStatus2) {
        // // 逾期
        // if (card1.getCardStatus() != 0) {
        // if (d1 >= d2) {
        // return -1;
        // } else {
        // return 1;
        // }
        // } else {
        // if (cardStatus1 == cardStatus2 && cardStatus1 == 2) {
        // if (cardDay1 > cardDay2) {
        // return -1;
        // } else {
        // return 1;
        // }
        // } else if (cardStatus1 == cardStatus2 && cardStatus1 == 0) {
        // if (cardDay1 > cardDay2) {
        // return 1;
        // } else {
        // return -1;
        // }
        // }
        // }
        // }
        // return 1;

    }
}
