package com.pay.aile.bill.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pay.aile.bill.entity.CreditBillDetail;

/**
 * <p>
 * 邮件解析模板 Mapper 接口
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
public interface CreditBillDetailMapper extends BaseMapper<CreditBillDetail> {
    /***
     * 批量插入
     *
     * @param creditBillDetailList
     */
    void batchInsert(@Param(value = "year") int year, @Param(value = "month") int month,
            @Param(value = "list") List<CreditBillDetail> creditBillDetailList);

    void batchInsertBase(@Param(value = "list") List<CreditBillDetail> creditBillDetailList);
}