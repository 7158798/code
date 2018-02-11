package com.pay.card.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.pay.card.model.CreditCard;

public interface CreditCardDao extends JpaRepository<CreditCard, Long>, JpaSpecificationExecutor<CreditCard> {

    @Query("select c from CreditCard c where c.numbers = ?1 and id<>?2 ")
    public CreditCard findByNumbers(String numbers, Long cardId);

    @Transactional
    @Query("SELECT cc from CreditCard cc where EXISTS (SELECT cucr from CreditUserCardRelation cucr where cucr.cardId = cc.id and cucr.userId = ?1 and cucr.status = 1 )")
    public
            List<CreditCard> findCreditCard(Long userId);

    @Query("select cc from CreditCard cc where cc.completeNumbers = ?1 and cc.cardholder = ?2 and cc.userId = ?4 and cc.bank.id = ?3")
    public CreditCard findCreditCardBy(String numbers, String cardholder, Long bankId, Long userId);

    @Query("SELECT cc from CreditCard cc where EXISTS (SELECT cucr from CreditUserCardRelation cucr where cucr.cardId = cc.id and cucr.cardId = ?1 and cucr.userId = ?2 and cucr.status = 1 )")
    public
            CreditCard findCreditCardByCardId(Long cardId, Long userId);

    @Query("select c from CreditCard c where ( c.source = 0 and c.billDay=?1) or (c.source = 1 and EXISTS( select t.id from CreditUserCardRelation t where t.status=1 and t.cardId = c.id and t.billDay=?2) ) ")
    public
            List<CreditCard> findCreditCardListByBillDay(String billDay1, Integer billDay);

    @Query("select c from CreditCard c where c.dueDate<=?1 and EXISTS( select t.id from CreditUserCardRelation t where  t.status=1  and t.cardId = c.id )")
    public
            List<CreditCard> findCreditCardListByDueDay(Date dueDate);

    @Transactional
    @Modifying
    @Query("update CreditCard set name = ?1 where id = ?2")
    public void updateCardNameById(String cardName, Long cardId);

    @Transactional
    @Modifying
    @Query("update CreditCard set numbers=?1 , completeNumbers=?1 , status=0 where id = ?2")
    public void updateCardNumbersById(String numbers, Long cardId);

    @Transactional
    @Modifying
    @Query("update CreditCard cc set repayment = repayment + ?4 ,update_date = ?3 where  EXISTS "
            + "(SELECT cucr from CreditUserCardRelation cucr where cc.id = cucr.cardId and cc.id = ?1 and cucr.userId = ?2 and cucr.status = 1)")
    public
            void updateCardRePayMent(Long cardId, Long userId, Date updateDate, BigDecimal rePayMent);

    // @Transactional
    // @Modifying
    // @Query("update CreditCard set numbers=?1 ,completeNumbers=?1 where id =
    // ?2")
    // public void updateCardStatusById(String numbers, Long cardId);

    @Transactional
    @Modifying
    @Query("update CreditCard cc set cc.status = 0,cc.updateDate = ?3 where cc.id = ?1 and cc.userId = ?2")
    public void updateCardStatusById(Long cardId, Long userId, Date updateDate);

}
