/**
 * @Title: RequestToBean.java
 * @Package com.pay.card.api.resolver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年12月6日
 * @version V1.0
 */

package com.pay.card.api.resolver;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import com.pay.card.model.CreditBill;
import com.pay.card.model.CreditRepayment;
import com.pay.card.model.CreditSet;

/**
 * @ClassName: RequestToBean
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月6日
 */

public class RequestToBean {

    public static CreditRepayment RequestToRepayment(HttpServletRequest request) {
        CreditRepayment repayment = new CreditRepayment();

        String id = request.getParameter("id");
        if (StringUtils.hasText(id)) {
            repayment.setId(new Long(id));
        }

        String amount = request.getParameter("amount");
        if (StringUtils.hasText(amount)) {
            repayment.setAmount(new BigDecimal(amount));
        }

        String type = request.getParameter("type");
        if (StringUtils.hasText(type)) {
            repayment.setType(new Integer(type));
        }
        String cardId = request.getParameter("cardId");
        if (StringUtils.hasText(cardId)) {
            repayment.setCardId(new Long(cardId));
        }

        String month = request.getParameter("month");
        if (StringUtils.hasText(month)) {
            repayment.setMonth(month);
        }
        String year = request.getParameter("year");
        if (StringUtils.hasText(year)) {
            repayment.setYear(year);
        }
        String billId = request.getParameter("billId");
        if (StringUtils.hasText(billId)) {
            CreditBill bill = new CreditBill();
            bill.setId(new Long(billId));

            repayment.setBill(bill);
        }
        return repayment;
    }

    public static CreditSet RequestToSet(HttpServletRequest request) {
        CreditSet set = new CreditSet();

        String id = request.getParameter("id");
        if (StringUtils.hasText(id)) {
            set.setId(new Long(id));
        }

        String advanceDay = request.getParameter("advanceDay");
        if (StringUtils.hasText(advanceDay)) {
            set.setAdvanceDay(new Integer(advanceDay));
        }

        String billdayReminder = request.getParameter("billdayReminder");
        if (StringUtils.hasText(billdayReminder)) {
            set.setBilldayReminder(new Integer(billdayReminder));
        }
        String outBillReminder = request.getParameter("outBillReminder");
        if (StringUtils.hasText(billdayReminder)) {
            set.setOutBillReminder(new Integer(outBillReminder));
        }
        String overdueReminder = request.getParameter("overdueReminder");
        if (StringUtils.hasText(overdueReminder)) {
            set.setOverdueReminder(new Integer(overdueReminder));
        }
        String repaymentReminder = request.getParameter("repaymentReminder");
        if (StringUtils.hasText(repaymentReminder)) {
            set.setRepaymentReminder(new Integer(repaymentReminder));
        }
        String times = request.getParameter("times");
        if (StringUtils.hasText(times)) {
            set.setTimes(new Integer(times));
        }
        return set;
    }
}
