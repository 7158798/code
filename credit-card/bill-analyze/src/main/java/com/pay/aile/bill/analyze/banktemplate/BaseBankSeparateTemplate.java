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
 * @see: BaseBankSeparateTemplate 多账户分开还款账单类型基类
 *
 *       账单中抓取到的卡号与账单数量始终保持一致
 *
 *       账单中和账单明细中都存储有卡号字段,以标识自己属于哪一张卡
 *
 *       保存时就根据卡号区分账单明细与账单,账单与卡的关联关系
 *
 * @version 2017年12月1日 上午10:50:44
 * @author chao.wang
 */
public abstract class BaseBankSeparateTemplate extends BaseBankTemplate {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    @Override
    protected void handleResultInternal(AnalyzeParamsModel apm) {
        Long emailId = apm.getEmailId();
        int year = 0;
        int month = 0;
        // 保存或更新卡信息
        List<CreditCard> cardList = apm.getResult().getCardList();// 保存账单
        List<CreditBill> billList = apm.getResult().getBillList();
        if (billList.size() < cardList.size()) {
            for (int i = billList.size(); i < cardList.size(); i++) {
                CreditBill bill = new CreditBill();
                BeanUtils.copyProperties(billList.get(0), bill, CreditBill.class);
                billList.add(bill);
            }
        }
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
        if (!saveCardList.isEmpty()) {
            saveCardList = creditCardService.saveOrUpateCreditCard(saveCardList);
        }
        logger.debug("saveCardList === {}", saveCardList);

        for (int i = 0; i < saveBillList.size(); i++) {
            CreditBill bill = saveBillList.get(i);
            bill.setCardId(saveCardList.get(i).getId());
            bill.setCardNumbers(saveCardList.get(i).getCompleteNumbers());
            bill.setEmailId(emailId);
            bill.setSentDate(apm.getSentDate());
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

            // 根据明细中的卡号和账单中的卡号进行对应
            List<CreditBillDetail> detailList = apm.getResult().getDetailList();
            List<CreditBillDetailRelation> relationList = new ArrayList<CreditBillDetailRelation>();

            if (!detailList.isEmpty()) {
                logger.info("before saveDetailList={}", detailList);
                // 保存明细
                creditBillDetailService.batchSaveBillDetail(year, month, detailList);
                logger.info("after saveDetailList={}", detailList);
                saveBillList.forEach(bill -> {
                    String cardNo = bill.getCardNumbers();
                    detailList.stream().filter(e -> e.getCardNumbers().equals(cardNo)).forEach(detail -> {
                        if (bill.getId() != null) {
                            if (bill.getId() != null) {
                                CreditBillDetailRelation relation = new CreditBillDetailRelation();
                                relation.setBillId(bill.getId());
                                relation.setBillDetailId(detail.getId());
                                relationList.add(relation);
                            }

                        }

                    });
                });
                // 保存账单和明细的关系
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
        saveCardList.forEach(card -> {
            CreditUserCardRelation cardRelation = new CreditUserCardRelation();
            cardRelation.setCardId(card.getId());
            cardRelation.setUserId(card.getUserId());
            cardRelation.setStatus(apm.getIsNew() ? CardStatus.AVAILABLE.value : CardStatus.NEW.value);

            // cardRelation.setStatus(CardStatus.AVAILABLE.value);
            cardRelationList.add(cardRelation);
        });
        // 保存卡片和用户关系
        creditUserCardRelationService.batchSave(cardRelationList);
        // 修改积分
        updateIntegral(saveBillList, apm);
    }

}
