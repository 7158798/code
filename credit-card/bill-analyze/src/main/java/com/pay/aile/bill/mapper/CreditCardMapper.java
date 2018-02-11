package com.pay.aile.bill.mapper;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pay.aile.bill.entity.CreditCard;

/**
 * <p>
 * 卡分类 Mapper 接口
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
public interface CreditCardMapper extends BaseMapper<CreditCard> {
    void batchInsert(List<CreditCard> cardList);

    CreditCard findByUnioue(CreditCard card);

    void insertOrUpdate(CreditCard card);

    void updateIntegral(Long cardId);
}