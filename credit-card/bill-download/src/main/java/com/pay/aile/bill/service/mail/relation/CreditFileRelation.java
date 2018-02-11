package com.pay.aile.bill.service.mail.relation;

import java.util.List;
import java.util.Map;

import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.entity.CreditNativeEmail;

/***
 * FileRelation.java
 *
 * @author shinelon
 *
 * @date 2017年11月3日
 *
 */
public interface CreditFileRelation {
    public List<CreditFile> selectDownloadedCreditFiles(Map<String, Object> email);

    /**
     *
     * @Title: copyNotExitsCreditFile
     * @Description:复制用户和邮件的关系
     * @param creditFileList
     * @param creditEmail
     * @return void @throws
     */
    void copyNotExitsCreditFile(List<CreditFile> creditFileList, CreditEmail creditEmail);

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
    void saveNotExitsCreditFile(List<CreditFile> creditFileList, CreditEmail creditEmail);
    
    
    void saveNotExitsCreditFile(List<CreditFile> creditFileList, CreditNativeEmail creditEmail);
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

    List<CreditFile> selectDownloadedCreditFiles(String emailAddr);

    /***
     * 更新邮件关系
     *
     * @param creditFile
     * @return
     */
    Integer updateCreditFile(CreditFile creditFile);
}
