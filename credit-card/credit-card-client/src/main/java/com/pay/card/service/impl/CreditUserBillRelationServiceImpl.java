package com.pay.card.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pay.card.dao.CreditUserBillRelationDao;
import com.pay.card.model.CreditUserBillRelation;
import com.pay.card.service.CreditUserBillRelationService;

@Service
public class CreditUserBillRelationServiceImpl implements CreditUserBillRelationService {
    @Autowired
    private CreditUserBillRelationDao creditUserBillRelationDao;

    @Override
    public List<CreditUserBillRelation> findCreditUserBillRelation(CreditUserBillRelation creditUserBillRelation)
            throws Exception {
        return creditUserBillRelationDao.findAll(getSpecification(creditUserBillRelation));
    }

    @Override
    public void saveCreditUserBillRelation(CreditUserBillRelation creditUserBillRelation) throws Exception {
        creditUserBillRelationDao.saveAndFlush(creditUserBillRelation);
    }

    @Override
    public void updateNewStatus(List<Long> billId, Long userId) throws Exception {

        creditUserBillRelationDao.updateNewStatus(billId, userId, new Date());
    }

    /*
     * (非 Javadoc)
     *
     *
     * @param billId
     *
     * @param userId
     *
     * @throws Exception
     *
     * @see
     * com.pay.card.service.CreditUserBillRelationService#updateNewStatus(java.
     * util.List, java.lang.Long)
     */

    private Specification<CreditUserBillRelation> getSpecification(CreditUserBillRelation creditUserBillRelation) {
        return new Specification<CreditUserBillRelation>() {
            @Override
            public Predicate toPredicate(Root<CreditUserBillRelation> root, CriteriaQuery<?> query,
                    CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                list.add(cb.equal(root.get("userId").as(Long.class), creditUserBillRelation.getUserId()));
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };

    }

}
