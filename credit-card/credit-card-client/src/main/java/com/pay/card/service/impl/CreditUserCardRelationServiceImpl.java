package com.pay.card.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pay.card.dao.CreditUserCardRelationDao;
import com.pay.card.model.CreditUserCardRelation;
import com.pay.card.service.CreditUserCardRelationService;

@Service
public class CreditUserCardRelationServiceImpl implements CreditUserCardRelationService {

    @Autowired
    private CreditUserCardRelationDao creditUserCardRelationDao;

    @Override
    public List<CreditUserCardRelation> findCreditUserCardRelation(CreditUserCardRelation creditUserCardRelation) {

        return creditUserCardRelationDao.findAll(getSpecification(creditUserCardRelation));
    }

    private Specification<CreditUserCardRelation> getSpecification(CreditUserCardRelation creditUserCardRelation) {
        return new Specification<CreditUserCardRelation>() {
            @Override
            public Predicate toPredicate(Root<CreditUserCardRelation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (creditUserCardRelation.getUserId() != null) {
                    list.add(cb.equal(root.get("userId").as(Long.class), creditUserCardRelation.getUserId()));
                }
                if (creditUserCardRelation.getCardId() != null) {
                    list.add(cb.equal(root.get("cardId").as(Long.class), creditUserCardRelation.getCardId()));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };

    }

    @Override
    public void saveCreditUserCardRelation(CreditUserCardRelation creditUserCardRelation) throws Exception {
        creditUserCardRelationDao.saveAndFlush(creditUserCardRelation);
    }

}
