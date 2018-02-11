package com.pay.aile.bill.analyze.banktemplate.cmb;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankTemplate;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.entity.CreditUserCardRelation;
import com.pay.aile.bill.entity.CreditUserEmailRelation;
import com.pay.aile.bill.enums.CardStatus;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.exception.AnalyzeBillException;
import com.pay.aile.bill.model.AnalyzeParamsModel;

/**
 *
 * @author Charlie
 * @description 招商银行消费提醒邮件账单
 */
@Service
public class CMBDetailTemplate extends BaseBankTemplate implements AbstractCMBTemplate {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(22L);
            rules.setDetails("\\d{4} \\d{8} \\d{2}:\\d{2}:\\d{2} \\S+ \\S+ -?\\d+\\.?\\d*");
            rules.setCardNumbers("0");
            rules.setTransactionDate("1");
            rules.setTransactionDescription("4");
            rules.setTransactionAmount("5");
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
        }
    }

    @Override
    protected void checkCardAndBill(AnalyzeParamsModel apm) throws AnalyzeBillException {
        if (apm.getResult().getCardList().isEmpty()) {
            throw new AnalyzeBillException("未抓取到卡号");
        }
        // 检查是否包含卡号和持卡人
        for (CreditCard card : apm.getResult().getCardList()) {
            if (!StringUtils.hasText(card.getNumbers())) {
                throw new AnalyzeBillException("无法获取卡号");
            }
            if (!StringUtils.hasText(card.getCardholder())) {
                throw new AnalyzeBillException("无法获取持卡人");
            }
        }
        if (apm.getResult().getDetailList().isEmpty()) {
            throw new AnalyzeBillException("无法获取明细");
        }
    }

    @Transactional
    @Override
    protected void handleResultInternal(AnalyzeParamsModel apm) {
        Long emailId = apm.getEmailId();
        List<CreditCard> cardList = apm.getResult().getCardList();
        List<CreditBillDetail> detailList = apm.getResult().getDetailList();
        // 查询邮件和用户的关系
        List<CreditUserEmailRelation> emailRelationList = new ArrayList<>();
        if (apm.getIsNew()) {
            CreditUserEmailRelation cuer = new CreditUserEmailRelation();
            cuer.setUserId(apm.getUserId());
            emailRelationList.add(cuer);
        } else {
            emailRelationList = userEmailRelationMapper.findByEmail(emailId);
        }
        CreditCard saveCard = cardList.get(0);
        saveCard.setStatus(CardStatus.UNAVAILABLE.value);
        saveCard.setCompleteNumbers(saveCard.getNumbers());
        saveCard.setNumbers(DEFAULT_NUMBERS);

        List<CreditCard> saveCardList = new ArrayList<>();
        for (CreditUserEmailRelation relation : emailRelationList) {
            CreditCard cardItem = new CreditCard();
            BeanUtils.copyProperties(saveCard, cardItem, CreditCard.class);
            cardItem.setUserId(relation.getUserId());
            saveCardList.add(cardItem);
        }
        saveCardList = creditCardService.saveOrUpateCreditCard(saveCardList);
        logger.debug("saveCardList == {}", saveCardList);
        // 保存明细
        List<CreditBillDetail> saveDetailList = new ArrayList<>();
        if (!detailList.isEmpty()) {
            saveCardList.forEach(card -> {
                detailList.forEach(detail -> {
                    CreditBillDetail saveDetail = new CreditBillDetail();
                    BeanUtils.copyProperties(detail, saveDetail, CreditBillDetail.class);
                    saveDetail.setCardId(card.getId());
                    saveDetailList.add(saveDetail);
                });
            });
            try {
                creditBillDetailService.batchSaveBillDetail(0, 0, saveDetailList);
            } catch (DuplicateKeyException e) {
                logger.warn("招商银行消费提醒账单重复解析,email={},userId={},fileName={},subject={}", apm.getEmail(), apm.getUserId(),
                        apm.getFileName(), apm.getSubject());
            }

        }

        // 卡片和用户的关系
        List<CreditUserCardRelation> cardRelationList = new ArrayList<CreditUserCardRelation>();
        // 保存卡片和用户关系
        for (CreditCard card : saveCardList) {
            CreditUserCardRelation cardRelation = new CreditUserCardRelation();
            cardRelation.setCardId(card.getId());
            cardRelation.setUserId(card.getUserId());
            cardRelation.setStatus(CardStatus.UNAVAILABLE.value);
            cardRelationList.add(cardRelation);
        }
        // 保存
        creditUserCardRelationService.batchSave(cardRelationList);
        apm.getResult().setCardList(null);
    }

    @Override
    protected void setCard(List<CreditCard> cardList, List<CreditBill> billList, AnalyzeParamsModel apm) {
        cardList.forEach(card -> {
            card.setBankId(new Long(apm.getBankId()));
            card.setEmail(apm.getEmail());
            if (card.getCredits() != null && card.getCredits().doubleValue() > 0) {
                // 抓取到额度了,则使用抓取到的额度,不允许自行修改额度
                card.setCreditsType(0);// 0表示额度不可编辑
            } else {
                card.setCreditsType(1);// 1标识额度可编辑
            }
        });
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.CMB_DETAIL;
    }
}
