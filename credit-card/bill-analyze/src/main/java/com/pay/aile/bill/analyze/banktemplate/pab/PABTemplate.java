package com.pay.aile.bill.analyze.banktemplate.pab;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.aile.bill.analyze.banktemplate.BaseBankTemplate;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditBillDetailRelation;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.entity.CreditUserBillRelation;
import com.pay.aile.bill.entity.CreditUserCardRelation;
import com.pay.aile.bill.entity.CreditUserEmailRelation;
import com.pay.aile.bill.enums.CardStatus;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.model.AnalyzeParamsModel;

/**
 *
 * @author Charlie
 * @description 平安银行解析模板
 */
@Service
public class PABTemplate extends BaseBankTemplate implements AbstractPABTemplate {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(1L);
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
            rules.setYearMonth("本期账单日 \\d{4}-\\d{2}-\\d{2}");
            rules.setCardNumbers("\\*+\\d{4}");
            rules.setBillDay("本期账单日 \\d{4}-\\d{2}-\\d{2}");
            rules.setDueDate("本期还款日 \\d{4}-\\d{2}-\\d{2}");
            rules.setCredits("信用额度 \\d+\\.?\\d*");
            rules.setCash("取现额度 \\d+\\.?\\d*");
            rules.setCurrentAmount("本期最低应还金额 \\d+\\.?\\d*");
            rules.setMinimum("本期最低应还金额 \\d+\\.?\\d* \\S+\\d+\\.?\\d* \\d+\\.?\\d*");
            rules.setDetails("\\d{4}-\\d{2}-\\d{2} \\d{4}-\\d{2}-\\d{2} \\S+ -?\\d+\\.?\\d*");
            rules.setIntegral("查看积分详情 \\^|&\\S+ \\d+");
            rules.setTransactionDate("0");
            rules.setBillingDate("1");
            rules.setTransactionDescription("2");
            rules.setTransactionAmount("3");
        }
    }

    @Transactional
    @Override
    protected void handleResultInternal(AnalyzeParamsModel apm) {
        Long emailId = apm.getEmailId();
        int year = 0;
        int month = 0;
        List<CreditCard> cardList = apm.getResult().getCardList();
        List<CreditBill> billList = apm.getResult().getBillList();
        List<CreditCard> saveCardList = new ArrayList<>();
        List<CreditBill> saveBillList = new ArrayList<>();

        List<CreditBillDetail> detailList = apm.getResult().getDetailList();
        CreditCard saveCard = null;
        CreditBill saveBill = null;
        // 根据明细中出现的卡号的数量最多的选出一个卡号进行保存
        saveCard = cardList.get(0);
        saveBill = billList.get(0);
        // 保存或更新卡信息
        saveCard.setStatus(apm.getIsNew() ? CardStatus.AVAILABLE.value : CardStatus.NEW.value);
        // saveCard.setStatus(CardStatus.AVAILABLE.value);

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
            CreditCard cardItem = new CreditCard();
            BeanUtils.copyProperties(saveCard, cardItem, CreditCard.class);
            cardItem.setUserId(relation.getUserId());
            saveCardList.add(cardItem);
        }
        if (!saveCardList.isEmpty()) {
            saveCardList = creditCardService.saveOrUpateCreditCard(saveCardList);
        }
        logger.debug("saveCardList == {}", saveCardList);
        // 保存账单
        saveBill.setCardId(saveCard.getId());
        saveBill.setEmailId(emailId);
        saveBill.setSentDate(apm.getSentDate());
        saveBill.setFileId(apm.getFileId());
        year = Integer.valueOf(saveBill.getYear());
        month = Integer.valueOf(saveBill.getMonth());
        for (CreditCard card : saveCardList) {
            CreditBill bill = new CreditBill();
            BeanUtils.copyProperties(saveBill, bill, CreditBill.class);
            bill.setCardId(card.getId());
            saveBillList.add(bill);
        }

        boolean repeatBill = false;
        repeatBill = saveBillList(saveBillList, apm);
        logger.debug("repeatBill={},saveBillList == {}", repeatBill, saveBillList);

        if (!repeatBill) {
            // 保存明细
            if (!detailList.isEmpty()) {
                creditBillDetailService.batchSaveBillDetail(year, month, detailList);
            }
            // 保存账单和明细的关系
            List<CreditBillDetailRelation> relationList = new ArrayList<CreditBillDetailRelation>();
            for (CreditBillDetail detail : detailList) {
                saveBillList.forEach(bill -> {
                    CreditBillDetailRelation relation = new CreditBillDetailRelation();
                    if (bill.getId() != null) {
                        relation.setBillId(bill.getId());
                        relation.setBillDetailId(detail.getId());
                        relationList.add(relation);
                    }

                });
            }

            creditBillDetailRelationService.batchSaveBillDetailRelation(year, month, relationList);

            // 账单和用户的关系
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
            // 保存账单和用户关系。
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

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.PAB_DEFAULT;
    }

}
