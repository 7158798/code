package com.pay.aile.bill.service.mail.relation.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.entity.CreditNativeEmail;
import com.pay.aile.bill.entity.CreditUserEmailRelation;
import com.pay.aile.bill.entity.CreditUserFileRelation;
import com.pay.aile.bill.enums.CommonStatus;
import com.pay.aile.bill.mapper.CreditFileMapper;
import com.pay.aile.bill.mapper.CreditUserEmailRelationMapper;
import com.pay.aile.bill.mapper.CreditUserFileRelationMapper;
import com.pay.aile.bill.service.mail.relation.CreditFileRelation;

/***
 * FileRelationImpl.java
 *
 * @author shinelon
 *
 * @date 2017年11月3日
 *
 */
@Service
public class CreditFileRelationImpl implements CreditFileRelation {
    private static final Logger logger = LoggerFactory.getLogger(CreditFileRelationImpl.class);

    @Autowired
    private CreditFileMapper creditFileMapper;

    @Autowired
    private CreditUserFileRelationMapper fileRelationMapper;

    @Autowired
    private CreditUserEmailRelationMapper emailRelationMapper;

    // @Transactional
    @Override
    public void copyNotExitsCreditFile(List<CreditFile> creditFileList, CreditEmail creditEmail) {

        List<CreditUserFileRelation> relationList = new ArrayList<CreditUserFileRelation>();
        // 保存文件和用户的关系
        creditFileList.forEach(f -> {

            CreditUserFileRelation relation = new CreditUserFileRelation();
            relation.setUserId(new Long(creditEmail.getUserId()));
            relation.setFileId(f.getId());
            relation.setCreateDate(new Date());
            relation.setUpdateDate(new Date());
            relationList.add(relation);

        });
        if (relationList.size() > 0) {
            fileRelationMapper.batchInsert(relationList);
        }

    }

    @Override
    public void saveCreditFile(CreditFile creditFile) {
        List<CreditFile> list = creditFileMapper
                .selectList(new EntityWrapper<CreditFile>().eq("file_name", creditFile.getFileName()));
        if (list.size() < 1) {
            creditFileMapper.insert(creditFile);
        }

    }

    @Transactional
    @Override
    public void saveNotExitsCreditFile(List<CreditFile> creditFileList, CreditEmail creditEmail) {
        List<String> fileNames = creditFileList.stream().map(e -> e.getFileName()).collect(Collectors.toList());
        List<CreditFile> exitslist = creditFileMapper
                .selectList(new EntityWrapper<CreditFile>().in("file_name", fileNames));
        List<String> exitsfileNames = exitslist.stream().map(e -> e.getFileName()).collect(Collectors.toList());
        List<CreditFile> insertList = creditFileList.stream().filter(e -> !exitsfileNames.contains(e.getFileName()))
                .collect(Collectors.toList());
        if (insertList.size() > 0) {
            creditFileMapper.batchInsert(insertList);
        }
        Map<String, Object> columnMap = new HashMap<String, Object>();
        columnMap.put("email_id", creditEmail.getId());
        List<CreditUserEmailRelation> emailRelationList = emailRelationMapper.selectByMap(columnMap);
        List<CreditUserFileRelation> relationList = new ArrayList<CreditUserFileRelation>();
        // 保存文件和用户的关系
        insertList.forEach(f -> {
            emailRelationList.forEach(e -> {
                CreditUserFileRelation relation = new CreditUserFileRelation();
                relation.setUserId(e.getUserId());
                relation.setFileId(f.getId());
                relation.setCreateDate(new Date());
                relation.setUpdateDate(new Date());
                relationList.add(relation);
            });

        });
        if (relationList.size() > 0) {
            fileRelationMapper.batchInsert(relationList);
        }

    }
    
    public void saveNotExitsCreditFile(List<CreditFile> creditFileList, CreditNativeEmail creditEmail) {
        List<String> fileNames = creditFileList.stream().map(e -> e.getFileName()).collect(Collectors.toList());
        List<CreditFile> exitslist = creditFileMapper
                .selectList(new EntityWrapper<CreditFile>().in("file_name", fileNames));
        List<String> exitsfileNames = exitslist.stream().map(e -> e.getFileName()).collect(Collectors.toList());
        List<CreditFile> insertList = creditFileList.stream().filter(e -> !exitsfileNames.contains(e.getFileName()))
                .collect(Collectors.toList());
        if (insertList.size() > 0) {
            creditFileMapper.batchInsert(insertList);
        }
      
        List<CreditUserFileRelation> relationList = new ArrayList<CreditUserFileRelation>();
        // 保存文件和用户的关系
        insertList.forEach(f -> {
        	CreditUserFileRelation relation = new CreditUserFileRelation();
            relation.setUserId(creditEmail.getUserId());
            relation.setFileId(f.getId());
            relation.setCreateDate(new Date());
            relation.setUpdateDate(new Date());
            relationList.add(relation);

        });
        if (relationList.size() > 0) {
            fileRelationMapper.batchInsert(relationList);
        }

    }

    
    @Override
    public List<CreditFile> selectCreditFiles(String emailAddr) {
        return creditFileMapper.selectList(
                new EntityWrapper<CreditFile>().eq("email", emailAddr).eq("status", CommonStatus.AVAILABLE.value));
    }

    @Override
    public List<CreditFile> selectCreditFiles(String email, String fileName) {
        return creditFileMapper.selectList(new EntityWrapper<CreditFile>().eq("email", email).eq("file_name", fileName)
                .eq("status", CommonStatus.AVAILABLE.value));
    }

    @Override
    public List<CreditFile> selectDownloadedCreditFiles(Map<String, Object> email) {
        return creditFileMapper.findByEmailAndUser(email);
    }

    @Override
    public List<CreditFile> selectDownloadedCreditFiles(String emailAddr) {
        return creditFileMapper.selectList(new EntityWrapper<CreditFile>().eq("email", emailAddr));
    }

    /*
     * (非 Javadoc)
     *
     *
     * @param email
     *
     * @return
     *
     * @see com.pay.aile.bill.service.mail.relation.CreditFileRelation#
     * selectFileByUserEmail(java.util.Map)
     */

    @Override
    public Integer updateCreditFile(CreditFile creditFile) {
        return creditFileMapper.updateById(creditFile);
    }

}
