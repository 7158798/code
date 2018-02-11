package com.pay.aile.bill.service;

import java.util.List;

import com.pay.aile.bill.entity.CreditCard;

public interface CreditCardService {
    /**
     *
     * @Title: findCreditCard
     * @Description:查询信用卡
     * @param card
     * @return CreditCard 返回类型 @throws
     */
    public CreditCard findCreditCard(CreditCard card);

    /**
     *
     * @Title: saveOrUpateCreditCard
     * @Description: 保存
     * @param card
     * @return Long 返回类型 @throws
     */
    public Long saveOrUpateCreditCard(CreditCard card);

    public List<CreditCard> saveOrUpateCreditCard(List<CreditCard> cardList);

    /**
     *
     * @Description 根据银行ID和email进行查询，若已存在，则更新，否则新增
     * @param card
     * @return
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public Long saveOrUpdateUnionCreditCard(CreditCard card);
}
