package com.pay.card.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.pay.card.model.CreditBill;

public interface CreditBillDao extends JpaRepository<CreditBill, Long>, JpaSpecificationExecutor<CreditBill> {

    @Transactional
    @Modifying
    @Query("update CreditBill set newStatus = '0',update_date = ?2 where id in ?1 ")
    public void updateNewStatus(List<Long> billId, Date upateDate);
}
