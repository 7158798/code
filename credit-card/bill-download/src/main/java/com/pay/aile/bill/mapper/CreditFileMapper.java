package com.pay.aile.bill.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pay.aile.bill.entity.CreditFile;

/**
 * <p>
 * 邮箱文件 Mapper 接口
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
public interface CreditFileMapper extends BaseMapper<CreditFile> {
    /***
     * 批量插入
     *
     * @param creditFileList
     */
    void batchInsert(List<CreditFile> creditFileList);

    List<CreditFile> findByEmailAndUser(Map<String, Object> email);

    List<CreditFile> findByRelation(String email);

}