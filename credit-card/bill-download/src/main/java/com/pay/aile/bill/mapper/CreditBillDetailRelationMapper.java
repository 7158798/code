package com.pay.aile.bill.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pay.aile.bill.entity.CreditBillDetailRelation;

/**
 * <p>
 * 邮件解析模板 Mapper 接口
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
public interface CreditBillDetailRelationMapper extends BaseMapper<CreditBillDetailRelation> {

    /***
     * 批量插入
     *
     * @param creditBillDetailList
     */
    void batchInsert(@Param(value = "year") int year, @Param(value = "month") int month,
            @Param(value = "list") List<CreditBillDetailRelation> creditBillDetailRelationList);

    List<CreditBillDetailRelation> findByBillId(@Param(value = "year") int year, @Param(value = "month") int month,
            @Param(value = "list") List<Long> billId);
}