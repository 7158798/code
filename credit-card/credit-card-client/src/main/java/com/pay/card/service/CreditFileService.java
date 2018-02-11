package com.pay.card.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CreditFileService {
    public List<Map<String, Object>> findCreditFileList(Integer page) throws Exception;

    /**
     * 查询解析失败的账单文件
     */
    public List<Map<String, Object>> findCreditFileList(String email, Date startTime, Date endTime, Integer page);
}
