package com.pay.card;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;
import com.pay.card.model.CreditCard;

public class CreditSetTest {

    public static void main(String[] args) {
        // CreditSet creditSet = new CreditSet();
        // creditSet.setId(2l);
        // creditSet.setAdvanceDay(6);
        // creditSet.setBilldayReminder(true);
        // creditSet.setCreateDate(new Date());
        // creditSet.setOutBillReminder(true);
        // creditSet.setOverdueReminder(true);
        // creditSet.setRepaymentReminder(true);
        // creditSet.setStatus(1);
        // creditSet.setTimes(10);
        // creditSet.setVersion(1l);
        // System.out.println(JSON.toJSONString(creditSet));

        // CreditRepayment creditRepayment = new CreditRepayment();
        // CreditBill bill = new CreditBill();
        // creditRepayment.setAmount(new BigDecimal(1000l));
        // creditRepayment.setCreateDate(new Date());
        // creditRepayment.setId(2l);
        // creditRepayment.setType(1);
        // creditRepayment.setVersion(1l);
        // creditRepayment.setBill(bill);
        // System.out.println(JSON.toJSONString(creditRepayment));

        CreditCard creditCard = new CreditCard();
        creditCard.setId(2890l);
        creditCard.setBillAmount(new BigDecimal(1000));
        creditCard.setBillDay(System.currentTimeMillis() + "");
        creditCard.setCardholder("abc");
        creditCard.setCustomerNo("8622632349");
        creditCard.setEmail("18518679659@139.com");
        System.out.println(JSON.toJSONString(creditCard));
    }

}
