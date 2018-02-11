package com.pay.aile.bill.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.model.CreditFileModel;

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

    /***
     * 查询文件列表
     *
     * @param email
     * @param fileName
     * @return
     */
    List<CreditFile> selectCreditFiles(String email, String fileName);

    List<CreditFileModel> selectOverTimeUnAnalyzedList();

    List<CreditFileModel> selectUnAnalyzedList();

    List<CreditFileModel> selectUnAnalyzedListByEmail(String email);

    Integer updateProcessResult(@Param("result") int result, @Param("id") Long id);
}