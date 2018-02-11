package com.pay.card.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.card.dao.CreditBillDetailDao;
import com.pay.card.model.CreditBillDetail;
import com.pay.card.service.CreditBillDetailService;

@Service
public class CreditBillDetailServiceImpl implements CreditBillDetailService {
    private static Logger logger = LoggerFactory.getLogger(CreditBillDetailServiceImpl.class);

    @Autowired
    private CreditBillDetailDao creditBillDetailDao;

    @Override
    public List<CreditBillDetail> findBillDetailList(Long billId, Long userId, String yearMonth) throws Exception {
        logger.info("账单id【" + billId + "】查询账单明细参数===========1:{}", yearMonth);
        if ("0".equals(yearMonth.substring(5, 6))) {
            yearMonth = yearMonth.split("_")[0] + "_" + yearMonth.split("_")[1].replaceAll("0", "");
        }
        logger.info("账单id【" + billId + "】查询账单明细参数===========2:{}", yearMonth);
        return creditBillDetailDao.findBillDetailList(billId, userId, yearMonth);
    }

    @Override
    public int findFutureBillAmountCount(Long cardId, Long userId) throws Exception {

        return creditBillDetailDao.findFutureBillAmountCount(cardId, userId);
    }

    @Override
    public List<CreditBillDetail> findFutureBillDetail(Long cardId, Long userId) throws Exception {

        return creditBillDetailDao.findFutureBillDetail(cardId, userId);
    }

}
