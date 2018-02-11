package com.pay.aile.bill.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay.aile.bill.entity.CreditBank;
import com.pay.aile.bill.enums.CommonStatus;
import com.pay.aile.bill.service.CreditBankService;

/**
 *
 * @ClassName: MemoryCache
 * @Description: 模板缓存
 * @author jinjing
 * @date 2017年11月10日
 *
 */
@Component
public class MemoryCache {

    /**
     * 银行缓存
     */
    static public Map<Long, String> bankCache = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CreditBankService creditBankService;

    @PostConstruct
    public void initBank() {
        CreditBank bank = new CreditBank();
        bank.setStatus(CommonStatus.AVAILABLE.value);
        List<CreditBank> bankList = creditBankService.getAllList(bank);

        for (CreditBank tempBank : bankList) {
            bankCache.put(tempBank.getId(), tempBank.getCode());
        }
        logger.info("***** init bankCache finish,bankCache={}", bankCache);
    }

}
