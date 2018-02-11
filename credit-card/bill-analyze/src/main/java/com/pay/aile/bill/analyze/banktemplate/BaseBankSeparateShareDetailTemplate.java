package com.pay.aile.bill.analyze.banktemplate;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditBillDetailRelation;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditUserBillRelation;
import com.pay.aile.bill.entity.CreditUserCardRelation;
import com.pay.aile.bill.entity.CreditUserEmailRelation;
import com.pay.aile.bill.enums.CardStatus;
import com.pay.aile.bill.model.AnalyzeParamsModel;

/**
 *
 * @Description:
 * @see: BaseBankSeparateShareDetailTemplate
 *
 *       多账户分开还款-共用明细的账单模板
 *
 *       无法区分每条明细对应到哪一张卡,所以认为每条明细属于每张卡每个账单
 * @version 2017年11月28日 上午11:13:24
 * @author chao.wang
 */
public abstract class BaseBankSeparateShareDetailTemplate extends BaseBankSeparateTemplate {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    @Override
    protected void handleResultInternal(AnalyzeParamsModel apm) {
        Long emailId = apm.getEmailId();
        int year = 0;
        int month = 0;
        // 保存或更新卡信息
        List<CreditCard> cardList = apm.getResult().getCardList();
        List<CreditBill> billList = apm.getResult().getBillList();
        List<CreditCard> saveCardList = new ArrayList<>();
        List<CreditBill> saveBillList = new ArrayList<>();
        // 查询邮件和用户的关系
        List<CreditUserEmailRelation> emailRelationList = new ArrayList<>();
        if (apm.getIsNew()) {
            CreditUserEmailRelation cuer = new CreditUserEmailRelation();
            cuer.setUserId(apm.getUserId());
            emailRelationList.add(cuer);
        } else {
            emailRelationList = userEmailRelationMapper.findByEmail(emailId);
        }
        for (CreditUserEmailRelation relation : emailRelationList) {
            for (CreditCard card : cardList) {
                CreditCard saveCard = new CreditCard();
                BeanUtils.copyProperties(card, saveCard, CreditCard.class);
                saveCard.setUserId(relation.getUserId());
                saveCardList.add(saveCard);
            }
            for (CreditBill bill : billList) {
                CreditBill saveBill = new CreditBill();
                BeanUtils.copyProperties(bill, saveBill, CreditBill.class);
                saveBillList.add(saveBill);
            }
        }

        saveCardList = creditCardService.saveOrUpateCreditCard(saveCardList);
        logger.debug("saveCardList == {}", saveCardList);
        // 保存账单
        for (int i = 0; i < saveBillList.size(); i++) {
            CreditBill bill = saveBillList.get(i);
            bill.setCardId(saveCardList.get(i).getId());
            bill.setEmailId(emailId);
            bill.setSentDate(apm.getSentDate());
            bill.setCardNumbers(saveCardList.get(i).getCompleteNumbers());
            bill.setFileId(apm.getFileId());
            if (year == 0) {
                year = Integer.valueOf(bill.getYear());
            }
            if (month == 0) {
                month = Integer.valueOf(bill.getMonth());
            }
        }

        boolean repeatBill = false;
        repeatBill = saveBillList(saveBillList, apm);
        logger.debug("repeatBill={},saveBillList == {}", repeatBill, saveBillList);

        if (!repeatBill) {

            List<CreditBillDetail> detailList = apm.getResult().getDetailList();
            List<CreditBillDetailRelation> relationList = new ArrayList<CreditBillDetailRelation>();
            if (!detailList.isEmpty()) {
                creditBillDetailService.batchSaveBillDetail(year, month, detailList);
                saveBillList.forEach(bill -> {
                    detailList.forEach(detail -> {
                        if (bill.getId() != null) {
                            CreditBillDetailRelation relation = new CreditBillDetailRelation();
                            relation.setBillId(bill.getId());
                            relation.setBillDetailId(detail.getId());
                            relationList.add(relation);
                        }

                    });
                });
                creditBillDetailRelationService.batchSaveBillDetailRelation(year, month, relationList);
            }

            // 保存账单和用户关系。
            List<CreditUserBillRelation> billRelationList = new ArrayList<CreditUserBillRelation>();
            for (int i = 0; i < saveCardList.size(); i++) {
                CreditCard card = saveCardList.get(i);
                CreditBill bill = saveBillList.get(i);
                if (bill.getId() != null) {
                    CreditUserBillRelation billRelation = new CreditUserBillRelation();
                    billRelation.setBillId(bill.getId());
                    billRelation.setUserId(card.getUserId());
                    billRelationList.add(billRelation);
                }

            }
            creditUserBillRelationService.batchSave(billRelationList);
        }
        // 卡片和用户的关系
        List<CreditUserCardRelation> cardRelationList = new ArrayList<CreditUserCardRelation>();
        // 保存卡片和用户关系
        saveCardList.forEach(card -> {
            CreditUserCardRelation cardRelation = new CreditUserCardRelation();
            cardRelation.setCardId(card.getId());
            cardRelation.setUserId(card.getUserId());
            cardRelation.setStatus(apm.getIsNew() ? CardStatus.AVAILABLE.value : CardStatus.NEW.value);

            // cardRelation.setStatus(CardStatus.AVAILABLE.value);
            cardRelationList.add(cardRelation);
        });
        // 保存
        creditUserCardRelationService.batchSave(cardRelationList);
        // 修改积分
        updateIntegral(saveBillList, apm);
    }

}
