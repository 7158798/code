package com.pay.aile.bill.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.pay.aile.bill.entity.CreditCardtype;
import com.pay.aile.bill.mapper.CreditCardtypeMapper;
import com.pay.aile.bill.service.CreditCardtypeService;

@Service
public class CreditCardTypeServiceImpl implements CreditCardtypeService {
    @Autowired
    private CreditCardtypeMapper creditCardtypeMapper;

    /**
     *
     * @Title: findList
     * @Description: 查询列表
     * @param cardType
     * @return List<CreditCardtype> 返回类型 @throws
     */
    public List<CreditCardtype> findList(CreditCardtype cardType) {
        //
        // Wrapper<CreditCardtype> wrapper = new
        // EntityWrapper<CreditCardtype>();
        // wrapper.addFilter(" status = {0} ", CommonStatus.AVAILABLE);
        // TODO 增加查询条件
        List<CreditCardtype> list = creditCardtypeMapper
                .selectList(new EntityWrapper<CreditCardtype>(cardType));
        return list;
    }

    @Override
    public CreditCardtype saveOrUpdate(CreditCardtype creditCardtype) {
        if (creditCardtype.getId() != null) {
            creditCardtypeMapper.updateById(creditCardtype);
        } else {
            creditCardtypeMapper.insert(creditCardtype);
        }
        return creditCardtype;
    }
}
