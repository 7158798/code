package com.pay.card.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pay.card.bean.CreditCardBean;
import com.pay.card.model.CreditCard;
import com.pay.card.model.CreditUserCardRelation;

public interface CreditCardService {
    /**
     * @Title: bathUpdateCreditCard @Description:批量修改 @param cardList @return
     *         void 返回类型 @throws
     */
    public void bathUpdateCreditCard(List<CreditCard> cardList) throws Exception;

    public CreditCard findCreditCard(CreditCard creditCard) throws Exception;

    // public CreditCard findCreditCard(CreditCardBean creditCard);

    /**
     * @Title: findCreditCardByCardId
     * @Description: 根据卡id,用户id查找卡信息
     * @param
     * @return
     */
    public CreditCard findCreditCardByCardId(Long cardId, Long id);

    /**
     * @Title: findCreditCardList
     * @Description:查询信用卡列表
     * @param creditCard
     * @return List<CreditCard> 返回类型 @throws
     */
    public List<CreditCard> findCreditCardList(CreditCard creditCard) throws Exception;

    /**
     * @Title: findCreditCardListByBillDay
     * @Description: 根据账单日查询卡
     * @param creditCard
     * @return List<CreditCard> 返回类型 @throws
     */
    public List<CreditCard> findCreditCardListByBillDay(CreditCard creditCard) throws Exception;

    public List<CreditCard> findCreditCardListByDueDay(Date dueDate) throws Exception;

    /**
     * @Title: saveOrUpdateCreditCard
     * @Description:
     * @param creditCard
     * @return CreditCard 返回类型 @throws
     */
    public CreditCard saveOrUpdateCreditCard(CreditCard creditCard, CreditCardBean creditCardBean, CreditUserCardRelation relation)
            throws Exception;

    /**
     * @Title: updateCardNameById
     * @Description: 根据id修改卡名称
     * @param
     * @return
     */
    public void updateCardNameById(String cardName, Long cardId) throws Exception;

    /**
     * @Title: updateCardRePayMent
     * @Description: 根据卡Id,用户Id修改已还款金额
     * @param
     * @return
     */
    public void updateCardRePayMent(Long cardId, Long userId, BigDecimal rePayMent) throws Exception;

    /**
     * @Title: updateCardStatusById
     * @Description: 根据id修改卡状态
     * @param cardId
     * @return
     */
    public void updateCardStatusById(Long cardId, Long userId) throws Exception;
}
