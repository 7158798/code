package com.pay.card.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.card.dao.StatisticsDao;
import com.pay.card.service.CreditFileService;

@Service
public class CreditFileServiceImpl implements CreditFileService {
    @Autowired
    private StatisticsDao statisticsDao;

    /**
     * 分页查询账单解析记录
     */
    @Override
    public List<Map<String, Object>> findCreditFileList(Integer page) throws Exception {
        return statisticsDao.findCreditFileList(page);
    }

    @Override
    public List<Map<String, Object>> findCreditFileList(String email, Date startTime, Date endTime, Integer page) {
        return statisticsDao.findCreditFileList(email, startTime, endTime, page);
    }

}
