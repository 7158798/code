package com.pay.card.service;

import java.util.List;

import com.pay.card.model.CreditBillDetail;

public interface CreditBillDetailService {

    /**
     * @Title: findBillDetailList
     * @Description: 根据billId跟账单月分页查询账单明细
     * @param billId
     * @param month
     * @param pageNumber
     * @param pageSize
     * @return List<CreditBillDetail>
     */
    public List<CreditBillDetail> findBillDetailList(Long billId, Long userId, String yearMonth) throws Exception;

    /**
     * @Title: findFutureBillAmountCount
     * @Description: 根据卡id,用户id查询未出账单总金额
     * @param
     * @return
     */
    public int findFutureBillAmountCount(Long cardId, Long userId) throws Exception;

    /**
     * @Title: findFutureBillDetail
     * @Description: 根据卡id,用户id查询未出账单明细
     * @param
     * @return List<CreditBillDetail>
     */
    public List<CreditBillDetail> findFutureBillDetail(Long cardId, Long userId) throws Exception;

}
