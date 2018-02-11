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
import com.pay.aile.bill.entity.CreditCardtype;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CommonStatus;
import com.pay.aile.bill.service.CreditBankService;
import com.pay.aile.bill.service.CreditCardtypeService;
import com.pay.aile.bill.service.CreditTemplateService;

/**
 *
 * @ClassName: TemplateCache
 * @Description: 模板缓存
 * @author jinjing
 * @date 2017年11月10日
 *
 */
@Component
public class TemplateCache {

    /**
     * 模板缓存
     */
    static public Map<String, CreditTemplate> templateCache = new HashMap<String, CreditTemplate>();
    /**
     * 银行缓存
     */
    static public Map<String, Long> bankCache = new HashMap<String, Long>();
    /**
     * 银行缓存
     */
    static public Map<String, Integer> bankRepaymentCache = new HashMap<String, Integer>();

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CreditTemplateService creditTemplateService;
    @Autowired
    private CreditCardtypeService creditCardtypeService;
    @Autowired
    private CreditBankService creditBankService;

    @PostConstruct
    public void initBank() {
        CreditBank bank = new CreditBank();
        bank.setStatus(CommonStatus.AVAILABLE.value);
        List<CreditBank> bankList = creditBankService.getAllList(bank);

        for (CreditBank tempBank : bankList) {
            bankCache.put(tempBank.getCode(), tempBank.getId());
            bankRepaymentCache.put(tempBank.getCode(), tempBank.getRepaymentCycle());

        }
        logger.info("***** init bankCache finish,bankCache={}", bankCache);
    }

    /**
     *
     * @Title: initTemplate
     * @Description: 初始化模板数据
     * @return void 返回类型 @throws
     */
    @PostConstruct
    public void initTemplate() {
        // 模板列表
        List<CreditTemplate> templateList = creditTemplateService.getList(new CreditTemplate());
        CreditCardtype cardType = new CreditCardtype();
        cardType.setStatus(CommonStatus.AVAILABLE.value);
        List<CreditCardtype> typeList = creditCardtypeService.findList(cardType);
        // 构建type的map
        Map<Long, String> typeMap = new HashMap<Long, String>();
        for (CreditCardtype type : typeList) {
            typeMap.put(type.getId(), type.getCode());
        }
        // 初始化template的map
        for (CreditTemplate template : templateList) {
            if (typeMap.containsKey(template.getCardtypeId())) {
                templateCache.put(typeMap.get(template.getCardtypeId()), template);
            }

        }

        logger.info("***** init templateCache finish,templateCache={}", templateCache);
    }
}
