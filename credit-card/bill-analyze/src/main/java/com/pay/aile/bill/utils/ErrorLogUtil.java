package com.pay.aile.bill.utils;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.service.CreditErrorLogService;

@Component
public class ErrorLogUtil {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CreditErrorLogService creditErrorLogService;

    /**
     * userId从何处取值
     *
     * @Description
     * @param apm
     * @param e
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void analyzeErrorLog(AnalyzeParamsModel apm, Exception error) {
        try {
            if (apm != null) {
                if (apm.getResult() != null && !apm.getResult().getBillList().isEmpty()) {
                    apm.getResult().getBillList().stream().filter(item -> item.getId() != null).forEach(bill -> {
                        creditErrorLogService.saveAnalyzeErrorLog(Long.valueOf(apm.getBankId()), apm.getCardtypeId(),
                                bill.getId(), apm.getEmail(), apm.getEmailId(), apm.getUserId(), error.getMessage());
                    });
                } else {
                    creditErrorLogService.saveAnalyzeErrorLog(Long.valueOf(apm.getBankId()), apm.getCardtypeId(), null,
                            apm.getEmail(), apm.getEmailId(), apm.getUserId(), error.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("analyzeErrorLog error!" + e.getMessage(), e);
        }
    }

}
