package com.pay.card.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay.card.BaseTest;

public class CreditBillDetailServiceImplTest extends BaseTest {

    @Autowired
    private CreditBillDetailService creditBillDetailService;

    @Test
    public void findTest() {
        // List<CreditBillDetail> list = creditBillDetailService.findBillDetailList(2404l, 1l, "1", 10, 10);
        // System.out.println(JSON.toJSONString(list));

        // int count = creditBillDetailService.findFutureBillAmountCount(2382l, 1l);
        // System.out.println(count);
        // List<CreditBillDetail> list = creditBillDetailService.findFutureBillDetail(2382l, 1l);

        // List<CreditBillDetail> list = creditBillDetailService.findBillDetailList(2405l, 3l, "2017_11");
        // System.out.println(JsonUtils.toJsonString(list));
    }

}
