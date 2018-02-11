
/**
* @Title: CardComparator.java
* @Package com.pay.card.utils
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月20日
* @version V1.0
*/

package com.pay.card.utils;

import java.util.Comparator;

import com.pay.card.view.CreditCardView;

/**
 * @ClassName: CardComparator
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月20日
 *
 */

public class CardComparator implements Comparator<CreditCardView> {
    @Override
    public int compare(CreditCardView card1, CreditCardView card2) {
        double billAmount1 = Double.parseDouble(card1.getBillAmount());
        double billAmount2 = Double.parseDouble(card2.getBillAmount());

        double credits1 = Double.parseDouble(card1.getCredits());
        double credits2 = Double.parseDouble(card2.getCredits());

        long free1 = card1.getFreeInterestPeriod();
        long free2 = card2.getFreeInterestPeriod();
        // 按照免息期排序
        if (free1 < free2) {
            return 1;
        } else if (free1 > free2) {
            return -1;
        } else {
            // 如果免息期相等，比较剩余金额
            double r1 = credits1 - billAmount1;
            double r2 = credits2 - billAmount2;
            if (r1 > r2) {
                return -1;
            } else if (r1 < r2) {
                return 1;
            } else {
                // 如果剩余金额相等则按照百分比排序
                if (r1 / billAmount1 > r2 / billAmount2) {
                    return -1;
                } else if (r1 / billAmount1 < r2 / billAmount2) {
                    return 1;
                } else {
                    return 0;
                }
            }

        }
    }

}
