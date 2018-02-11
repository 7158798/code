
/**
* @Title: CreditUserEmailRelationServiceImpl.java
* @Package com.pay.aile.bill.service.impl
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年11月30日
* @version V1.0
*/

package com.pay.aile.bill.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditUserEmailRelation;
import com.pay.aile.bill.mapper.CreditUserEmailRelationMapper;
import com.pay.aile.bill.service.CreditUserEmailRelationService;

/**
 * @ClassName: CreditUserEmailRelationServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月30日
 *
 */
@Service
public class CreditUserEmailRelationServiceImpl implements CreditUserEmailRelationService {
    @Autowired
    private CreditUserEmailRelationMapper creditUserEmailRelationMapper;

    @Override
    public List<CreditUserEmailRelation> findByEmail(Long emailId) {
        List<CreditUserEmailRelation> relation = creditUserEmailRelationMapper
                .selectList(new EntityWrapper<CreditUserEmailRelation>().eq("email_id", emailId));
        return relation;
    }

    @Override
    public CreditUserEmailRelation findByUser(String userId, Long emailId) {
        CreditUserEmailRelation relation = new CreditUserEmailRelation();
        relation.setUserId(new Long(userId));
        relation.setEmailId(emailId);
        relation = creditUserEmailRelationMapper.selectOne(relation);
        return relation;
    }

    /**
     *
     * @Title: findEmailByUser
     * @Description: 根据用户查询邮箱
     * @param userId
     *
     * @return List<CreditEmail> 返回类型 @throws
     */
    @Override
    public List<CreditEmail> findEmailByUser(String userId) {
        return creditUserEmailRelationMapper.findEmailByUser(userId);

    }

    /**
     * 保存
     */
    @Override
    public CreditUserEmailRelation saveOrUpdate(CreditUserEmailRelation relation) {
        if (relation.getId() != null) {
            creditUserEmailRelationMapper.updateById(relation);
        } else {
            creditUserEmailRelationMapper.insert(relation);
        }
        return relation;
    }
}
