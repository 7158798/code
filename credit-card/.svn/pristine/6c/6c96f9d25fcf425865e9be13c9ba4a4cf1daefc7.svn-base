package com.pay.card.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.pay.card.model.CreditBank;

public interface CreditBankDao extends JpaRepository<CreditBank, Integer>, JpaSpecificationExecutor<CreditBank> {

    @Query("select c from CreditBank c where c.code = ?1 ")
    public CreditBank findByCode(String code);
}
