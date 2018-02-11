package com.pay.aile.bill.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditErrorLog;
import com.pay.aile.bill.entity.CreditUserEmailRelation;
import com.pay.aile.bill.entity.CreditUserErrorLogRelation;
import com.pay.aile.bill.enums.ErrorType;
import com.pay.aile.bill.mapper.CreditErrorLogMapper;
import com.pay.aile.bill.mapper.CreditUserErrorLogRelationMapper;
import com.pay.aile.bill.service.CreditErrorLogService;
import com.pay.aile.bill.service.CreditUserEmailRelationService;

@Service
public class CreditErrorLogServiceImpl implements CreditErrorLogService {

    @Autowired
    private CreditErrorLogMapper creditErrorLogMapper;

    @Autowired
    private CreditUserErrorLogRelationMapper creditUserErrorLogRelationMapper;

    @Autowired
    private CreditUserEmailRelationService relationService;

    @Transactional
    @Override
    public void saveDownloadErrorLog(String email, Long emailId, Long userId, String msg) {
        saveErrorLog(ErrorType.download.name(), null, null, null, email, emailId, userId, msg);
    }

    @Transactional
    @Override
    public void saveErrorLog(String errorType, Long bankId, Long cardtypeId, Long billId, String email, Long emailId,
            Long userId, String msg) {
        CreditErrorLog errorLog = new CreditErrorLog();
        errorLog.setEmail(email);
        errorLog.setBillId(billId);
        errorLog.setErrorType(errorType);
        errorLog.setErrorMsg(msg);
        errorLog.setBankId(bankId);
        errorLog.setCardtypeId(cardtypeId);
        creditErrorLogMapper.insert(errorLog);
        List<CreditUserErrorLogRelation> relationList = new ArrayList<>();
        if (userId != null) {
            CreditUserErrorLogRelation relation = new CreditUserErrorLogRelation();
            relation.setErrorLogId(errorLog.getId());
            relation.setUserId(userId);
            relationList.add(relation);
        } else {
            // 根据email查询email对应的所有用户id,并为所有的用户id与此errorLog建立关联
            List<CreditUserEmailRelation> list = relationService.findByEmail(emailId);
            if (list != null && !list.isEmpty()) {
                list.forEach(item -> {
                    CreditUserErrorLogRelation relation = new CreditUserErrorLogRelation();
                    relation.setErrorLogId(errorLog.getId());
                    relation.setUserId(item.getUserId());
                    relationList.add(relation);
                });
            }
        }
        creditUserErrorLogRelationMapper.batchInsert(relationList);
    }

    @Transactional
    @Override
    public void saveLoginErrorLog(String email, Long emailId, Long userId, String msg) {
        saveErrorLog(ErrorType.login.name(), null, null, null, email, emailId, userId, msg);
    }

}
