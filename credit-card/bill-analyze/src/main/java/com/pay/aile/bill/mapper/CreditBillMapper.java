package com.pay.aile.bill.mapper;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pay.aile.bill.entity.CreditBill;

/**
 * <p>
 * 邮件解析模板 Mapper 接口
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
public interface CreditBillMapper extends BaseMapper<CreditBill> {

    void batchInsert(List<CreditBill> billList);

    void updateCreditBill(CreditBill bill);
}