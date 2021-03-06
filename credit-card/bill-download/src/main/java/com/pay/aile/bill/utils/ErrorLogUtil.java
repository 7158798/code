package com.pay.aile.bill.utils;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.service.CreditErrorLogService;

@Component
public class ErrorLogUtil {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CreditErrorLogService creditErrorLogService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void downloadErrorLog(CreditEmail creditEmail, Exception e) {
        try {
            creditErrorLogService.saveDownloadErrorLog(creditEmail.getEmail(), creditEmail.getId(),
                    Long.valueOf(creditEmail.getUserId()), e.getMessage());
        } catch (Exception ee) {
            logger.error("downloadErrorLog save error!" + ee.getMessage(), ee);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loginErrorLog(CreditEmail creditEmail, Exception e) {
        try {
            creditErrorLogService.saveLoginErrorLog(creditEmail.getEmail(), creditEmail.getId(),
                    Long.valueOf(creditEmail.getUserId()), e.getMessage());
        } catch (Exception ee) {

            logger.error("loginErrorLog save error!" + ee.getMessage(), ee);
        }
    }
}
