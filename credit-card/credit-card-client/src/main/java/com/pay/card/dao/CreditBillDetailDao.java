package com.pay.card.dao;

import java.util.List;

import com.pay.card.model.CreditBillDetail;

public interface CreditBillDetailDao {

    /**
     * @Title: findBillDatailCount
     * @Description: 根据账单id查询账单明细总数
     * @param
     * @return
     */
    public int findBillDatailCount(Long billId, String yearMonth);

    /**
     * @Title: findBillDetailList
     * @Description: 根据账单id,用户id,账单年月,查询账单明细
     * @param
     * @return
     */
    public List<CreditBillDetail> findBillDetailList(Long billId, Long userId, String yearMonth);

    /**
     * @Title: findFutureBillAmountCount
     * @Description: 根据卡id,用户id查询未出账单总金额
     * @param
     * @return
     */
    public int findFutureBillAmountCount(Long cardId, Long userId);

    /**
     * @Title: 根据卡Id,用户Id查询未出账单明细
     * @Description:
     * @param
     * @return
     */
    public List<CreditBillDetail> findFutureBillDetail(Long cardId, Long userId);

}
