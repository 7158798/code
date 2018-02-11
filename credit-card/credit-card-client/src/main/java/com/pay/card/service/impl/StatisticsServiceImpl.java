package com.pay.card.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.card.dao.StatisticsDao;
import com.pay.card.service.StatisticsService;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private StatisticsDao statisticsDao;

    @Override
    public List<Map<String, Object>> findCreditBillStatusList(Integer page, Integer num) {
        return statisticsDao.findCreditBillStatusList(page, num);
    }

    @Override
    public List<Map<String, Object>> findCreditInfoList(Integer page, Integer num) {
        return statisticsDao.findCreditInfoList(page, num);
    }

    @Override
    public List<Map<String, Object>> findCreditLoginLog(Integer page) {
        return statisticsDao.findCreditLoginLog(page);
    }

}
