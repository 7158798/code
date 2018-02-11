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
import org.springframework.transaction.annotation.Transactional;

import com.pay.card.dao.CreditUserEmailRelationDao;
import com.pay.card.dao.StatisticsDao;
import com.pay.card.model.CreditEmail;
import com.pay.card.model.CreditUserEmailRelation;
import com.pay.card.service.CreditUserEmailRelationService;

@Service
public class CreditUserEmailRelationServiceImpl implements CreditUserEmailRelationService {

    @Autowired
    private CreditUserEmailRelationDao creditUserEmailRelationDao;

    @Autowired
    private StatisticsDao statisticsDao;

    @Override
    public List<CreditUserEmailRelation> findCreditUserEmailRelation(CreditUserEmailRelation creditUserEmailRelation) throws Exception {

        return creditUserEmailRelationDao.findAll(getSpecification(creditUserEmailRelation));
    }

    @Override
    public List<CreditEmail> findEmailByUser(Long userId) throws Exception {

        return statisticsDao.findEmailByUser(userId);
    }

    private Specification<CreditUserEmailRelation> getSpecification(CreditUserEmailRelation creditUserEmailRelation) {
        return new Specification<CreditUserEmailRelation>() {
            @Override
            public Predicate toPredicate(Root<CreditUserEmailRelation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                list.add(cb.equal(root.get("userId").as(Long.class), creditUserEmailRelation.getUserId()));
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };
    }

    @Override
    public void saveCreditUserEmailRelation(CreditUserEmailRelation creditUserEmailRelation) throws Exception {
        creditUserEmailRelationDao.saveAndFlush(creditUserEmailRelation);

    }

    @Override
    @Transactional
    public void unbindEmail(Long userId, Long emailId) throws Exception {
        CreditEmail email = statisticsDao.findEmailById(emailId);
        statisticsDao.updateUserEmailStatus(userId, emailId);
        statisticsDao.updateUserCardStatus(userId, email.getEmail());
        statisticsDao.findEmailByUser(userId);
    }

}
