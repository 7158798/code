package com.pay.card.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay.card.BaseTest;
import com.pay.card.model.CreditRepayment;

public class CreditRepaymentServiceImplTest extends BaseTest {

    @Autowired
    private CreditRepaymentService creditRepaymentService;

    @Test
    public void findTest() {
        CreditRepayment creditRepayment = new CreditRepayment();
        creditRepayment.setCardId(2881l);

        // creditRepaymentService.findRePaymentDetail(creditRepayment);
        // System.out.println(list);

    }
}
