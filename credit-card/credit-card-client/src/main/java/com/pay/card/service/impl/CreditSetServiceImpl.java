package com.pay.card.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pay.card.dao.CreditSetDao;
import com.pay.card.dao.StatisticsDao;
import com.pay.card.model.CreditSet;
import com.pay.card.service.CreditSetService;
import com.pay.card.utils.BeanUtil;

@Service
public class CreditSetServiceImpl implements CreditSetService {
    @Autowired
    private CreditSetDao creditSetDao;
    @Autowired
    private StatisticsDao statisticsDao;

    @Override
    public CreditSet findCreditSet(CreditSet creditSet) throws Exception {
        creditSet = creditSetDao.findOne(getSpecification(creditSet));
        return creditSet;
    }

    /**
     * 分页查询提醒设置
     * @param creditSet
     * @param page
     * @return
     */
    @Override
    public List<Map<String, Object>> findCreditSetList(CreditSet creditSet, Integer page) throws Exception {
        return statisticsDao.findCreditSetList(creditSet, page);
    }

    public Specification<CreditSet> getSpecification(CreditSet creditSet) {
        return new Specification<CreditSet>() {
            @Override
            public Predicate toPredicate(Root<CreditSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                if (creditSet.getUserInfo() != null && creditSet.getUserInfo().getId() != null) {
                    list.add(cb.equal(root.get("userInfo").get("id").as(Long.class), creditSet.getUserInfo().getId()));
                }

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }

        };
    }

    @Override
    public CreditSet saveCreditSet(CreditSet creditSet) {
        creditSet = creditSetDao.saveAndFlush(creditSet);
        return creditSet;
    }

    @Override
    public CreditSet saveOrUpdate(CreditSet creditSet) throws Exception {
        CreditSet oldSet = creditSetDao.findOne(getSpecification(creditSet));
        if (oldSet == null) {
            creditSet = creditSetDao.saveAndFlush(creditSet);
        } else {
            creditSet.setUserInfo(null);
            BeanUtil.copyPropertiesIgnoreNull(creditSet, oldSet);
            creditSet = creditSetDao.saveAndFlush(oldSet);
        }

        return creditSet;
    }

}
