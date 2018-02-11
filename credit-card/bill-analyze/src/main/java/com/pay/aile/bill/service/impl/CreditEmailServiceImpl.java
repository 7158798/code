package com.pay.aile.bill.service.impl;

import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.enums.CommonStatus;
import com.pay.aile.bill.mapper.CreditEmailMapper;
import com.pay.aile.bill.service.CreditEmailService;

@Service
public class CreditEmailServiceImpl implements CreditEmailService {
    @Autowired
    private CreditEmailMapper creditEmailMapper;

    @Override
    public List<CreditEmail> getCreditEmails() {
        List<CreditEmail> list = creditEmailMapper
                .selectList(new EntityWrapper<CreditEmail>().eq("status", CommonStatus.AVAILABLE.value));
        list.forEach(e -> {
            e.setPassword(decodePassword(e.getPassword()));
        });
        return list;
    }

    @Override
    public List<CreditEmail> getEmailList(CreditEmail email) {

        Wrapper<CreditEmail> wrapper = Condition.create();

        wrapper.addFilter(" STATUS = {0} ", CommonStatus.AVAILABLE.value);
        if (StringUtils.isNotEmpty(email.getEmail())) {
            String sqlWhere = " email like {0} ";
            wrapper.addFilter(sqlWhere, email.getEmail() + "%");

        }
        return creditEmailMapper.selectList(wrapper);

    }

    /**
     *
     * @Title: saveOrUpdate
     * @Description: 保存
     * @param email
     * @return CreditEmail 返回类型 @throws
     */
    @Override
    public CreditEmail saveOrUpdate(CreditEmail email) {

        CreditEmail emailParam = new CreditEmail();
        emailParam.setEmail(email.getEmail());
        emailParam = creditEmailMapper.selectOne(emailParam);
        if (!email.isEncrypt()) {
            email.setPassword(encodePassword(email.getPassword()));
        }
        if (emailParam == null) {
            creditEmailMapper.insert(email);
        } else {
            email.setId(emailParam.getId());
            creditEmailMapper.updateById(email);
        }

        return email;

    }

    private String decodePassword(String password) {
        return new String(Base64.getDecoder().decode(password));
    }

    private String encodePassword(String password) {
        // 上SVN后使用公共加密工具
        /***
         * <groupId>com.lefu</groupId> <artifactId>commons-security</artifactId>
         * <version>1.0.31</version>
         */
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
}
