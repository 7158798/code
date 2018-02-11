package com.pay.aile.bill.service.mail.relation;

import java.util.List;

import com.pay.aile.bill.entity.CreditFile;

/***
 * FileRelation.java
 *
 * @author shinelon
 *
 * @date 2017年11月3日
 *
 */
public interface CreditFileRelation {
    /***
     * 保存邮件文件关系
     *
     * @param creditFile
     */
    void saveCreditFile(CreditFile creditFile);

    /***
     * 批量插入数据，排除file_name重复数据
     *
     * @param creditFileList
     */
    void saveNotExitsCreditFile(List<CreditFile> creditFileList);

    /***
     * 查询邮件关系
     *
     * @param emailAddr
     * @return
     */
    List<CreditFile> selectCreditFiles(String emailAddr);

    /***
     * 查询邮件关系
     *
     * @param email
     * @param fileName
     * @return
     */
    List<CreditFile> selectCreditFiles(String email, String fileName);

    /***
     * 更新邮件关系
     *
     * @param creditFile
     * @return
     */
    Integer updateCreditFile(CreditFile creditFile);
}
