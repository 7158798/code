package com.pay.aile.bill.service;

import java.util.List;

import com.pay.aile.bill.entity.CreditBank;

public interface CreditBankService {
    /***
     * 获取银行列表
     *
     * @param bank
     * @return
     */
    List<CreditBank> getAllList(CreditBank bank);

}
