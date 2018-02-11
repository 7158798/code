package com.pay.card.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.pay.card.model.CreditRepayment;

/**
 * @ClassName: CreditRepaymentDao
 * @Description: 信用卡还款
 * @author jinjing
 * @date 2017年11月16日
 */
public interface CreditRepaymentDao extends JpaRepository<CreditRepayment, Long>, JpaSpecificationExecutor<CreditRepayment> {

    @Query("select rc from CreditRepayment rc where rc.cardId = ?1 and rc.userInfo.id = ?2 and rc.status = 1 ORDER BY rc.year DESC, rc.month DESC")
    public
            List<CreditRepayment> findRePaymentDetail(Long cardId, Long userId);

    @Query("select rc from CreditRepayment rc where rc.userInfo.id = ?2 and rc.cardId = ?1 and rc.status = 1 GROUP BY rc.year , rc.month ORDER BY rc.year DESC,rc.month DESC ")
    public
            List<CreditRepayment> findRePaymentDetailYearMonth(Long cardId, Long userId);
}
