package com.pay.card.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay.card.BaseTest;
import com.pay.card.model.CreditUserInfo;
import com.pay.card.utils.LocalCacheUtil;

public class CreditUserInfoServiceImplTest extends BaseTest {

    @Autowired
    private CreditUserInfoService creditUserInfoService;

    @Test
    public void findTest() {
        CreditUserInfo creditUserInfo = new CreditUserInfo();
        creditUserInfo.setCustomerNo("8622632349");
        creditUserInfo.setPhoneNo("12345678901");
        creditUserInfo.setChannel("1001");
        // CreditUserInfo userInfo =
        // creditUserInfoService.findCreditUserInfo(creditUserInfo);
        // System.out.println(JSON.toJSONString(userInfo));
    }

    @Test
    public void localTest() {
        LocalCacheUtil.clear();
    }

    @Test
    public void saveTest() {
        CreditUserInfo creditUserInfo = new CreditUserInfo();
        creditUserInfo.setCustomerNo("8622632348");
        creditUserInfo.setPhoneNo("12345678902");
        creditUserInfo.setChannel("1002");
        creditUserInfo.setStatus(1);

        // Long id = creditUserInfoService.saveCreditUserInfo(creditUserInfo);
        // System.out.println(id);
    }

}
