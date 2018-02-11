package com.pay.aile.bill.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.mapper.CreditFileMapper;
import com.pay.aile.bill.model.CreditFileModel;
import com.pay.aile.bill.service.CreditFileService;

/**
 *
 * @author Charlie
 * @description
 */
@Service
public class CreditFileServiceImpl implements CreditFileService {

    @Autowired
    private CreditFileMapper creditFileMapper;

    @Override
    public CreditFile findById(Long id) {
        return creditFileMapper.selectById(id);
    }

    @Override
    public List<CreditFileModel> findOverTimeUnAnalyzedList() {
        return creditFileMapper.selectOverTimeUnAnalyzedList();
    }

    @Override
    public List<CreditFileModel> findUnAnalyzedList() {
        return creditFileMapper.selectUnAnalyzedList();
    }

    @Override
    public List<CreditFileModel> findUnAnalyzedListByEmail(CreditEmail eamil) {
        return creditFileMapper.selectUnAnalyzedListByEmail(eamil.getEmail());
    }

    @Transactional
    @Override
    public Integer updateProcessResult(int result, Long id) {
        return creditFileMapper.updateProcessResult(result, id);
    }

}
