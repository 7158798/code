package com.pay.card.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.pay.card.model.CreditUserCardRelation;

public interface CreditUserCardRelationDao extends JpaRepository<CreditUserCardRelation, Long>,
        JpaSpecificationExecutor<CreditUserCardRelation> {

    @Query("select cucr from CreditUserCardRelation cucr where cucr.cardId = ?2 and cucr.userId = ?1 and cucr.status = 1 ")
    public CreditUserCardRelation findCreditUserCardRelationOne(Long userId, Long cardId);

    @Query("select cucr from CreditUserCardRelation cucr where cucr.cardId = ?2 and cucr.userId = ?1 and cucr.status = 2")
    public CreditUserCardRelation findCreditUserCardRelationOneUpdate(Long userId, Long cardId);

    @Query("select c from CreditUserCardRelation c where c.userId =?1")
    public List<CreditUserCardRelation> findList(Long userId);

    @Transactional
    @Modifying
    @Query("update CreditUserCardRelation set repayment = 0,update_date = ?2 where card_id in ?1 ")
    public void updateCardRepayment(List<Long> cardIds, Date updateDate);

    @Transactional
    @Modifying
    @Query("update CreditUserCardRelation set repayment = repayment+?4,update_date = ?3 where card_id = ?1 and user_id = ?2 and status = 1")
    public
            void updateCardRepayment(Long cardId, Long userId, Date updateDate, BigDecimal repayment);

    @Transactional
    @Modifying
    @Query("update CreditUserCardRelation set status = '0',update_date = ?3 where card_id = ?1 and user_id = ?2 ")
    public void updateCardStatusById(Long cardId, Long userId, Date updateDate);

    @Transactional
    @Modifying
    @Query("update CreditUserCardRelation set status = '1',update_date = ?2 where card_id = ?1")
    public void updateCardStatusEnableById(Long cardId, Date updateDate);
}
