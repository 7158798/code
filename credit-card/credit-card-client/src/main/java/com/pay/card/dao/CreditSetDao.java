package com.pay.card.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.pay.card.model.CreditSet;

public interface CreditSetDao
        extends JpaRepository<CreditSet, Long>, JpaSpecificationExecutor<CreditSet> {

}
