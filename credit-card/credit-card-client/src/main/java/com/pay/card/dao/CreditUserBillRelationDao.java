package com.pay.card.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.pay.card.model.CreditUserBillRelation;

public interface CreditUserBillRelationDao
        extends JpaRepository<CreditUserBillRelation, Long>, JpaSpecificationExecutor<CreditUserBillRelation> {

    @Transactional
    @Modifying
    @Query("update CreditUserBillRelation set status = '0',update_date = ?3 where bill_id = ?1 and user_id = ?2")
    public void updateBillStatusById(Long billId, Long userId, Date upateDate);

    @Transactional
    @Modifying
    @Query("update CreditUserBillRelation set newStatus = '0',update_date = ?3 where bill_id in ?1 and user_id = ?2")
    public void updateNewStatus(List<Long> billId, Long userId, Date upateDate);

}
