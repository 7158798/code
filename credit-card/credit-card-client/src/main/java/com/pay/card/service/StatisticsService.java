package com.pay.card.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    public List<Map<String, Object>> findCreditBillStatusList(Integer page, Integer num);

    public List<Map<String, Object>> findCreditInfoList(Integer page, Integer num);

    public List<Map<String, Object>> findCreditLoginLog(Integer page);
}
