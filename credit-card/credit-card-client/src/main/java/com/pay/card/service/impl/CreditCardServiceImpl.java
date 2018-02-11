package com.pay.card.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pay.card.Exception.CreditClientException;
import com.pay.card.bean.CreditCardBean;
import com.pay.card.dao.CreditBankDao;
import com.pay.card.dao.CreditBillDao;
import com.pay.card.dao.CreditCardDao;
import com.pay.card.dao.CreditUserBillRelationDao;
import com.pay.card.dao.CreditUserCardRelationDao;
import com.pay.card.enums.ClientBankCodeEnum;
import com.pay.card.enums.CodeEnum;
import com.pay.card.enums.CreditBankCodeEnum;
import com.pay.card.enums.StatusEnum;
import com.pay.card.model.CreditBank;
import com.pay.card.model.CreditBill;
import com.pay.card.model.CreditCard;
import com.pay.card.model.CreditUserBillRelation;
import com.pay.card.model.CreditUserCardRelation;
import com.pay.card.service.CreditCardService;
import com.pay.card.utils.BeanUtil;
import com.pay.card.utils.DateUtil;
import com.pay.card.utils.UuidUtils;

/**
 * @Description: 信用卡接口实现
 * @see: CreditCardServiceImpl 此处填写需要参考的类
 * @version 2018年1月10日 下午2:05:51
 * @author zhibin.cui
 */
@Service
public class CreditCardServiceImpl implements CreditCardService {

    private static Logger logger = LoggerFactory.getLogger(CreditCardServiceImpl.class);

    @Autowired
    private CreditCardDao creditCardDao;
    @Autowired
    private CreditBillDao creditBillDao;

    @Autowired
    private CreditBankDao creditBankDao;

    @Autowired
    private CreditUserCardRelationDao creditUserCardRelationDao;

    @Autowired
    private CreditUserBillRelationDao creditUserBillRelationDao;

    @Override
    @Transactional
    public void bathUpdateCreditCard(List<CreditCard> cardList) throws Exception {

        creditCardDao.save(cardList);
        List<Long> cardIds = new ArrayList<Long>();
        cardList.forEach(card -> {
            cardIds.add(card.getId());
        });
        // 修改已还款金额
        if (cardIds.size() > 0) {
            Date now = new Date();
            creditUserCardRelationDao.updateCardRepayment(cardIds, now);
        }

    }

    @Override
    public CreditCard findCreditCard(CreditCard creditCard) throws Exception {
        // TODO 查询还款金额
        CreditCard card = creditCardDao.findOne(getSpecificationByUserId(creditCard));
        if (card != null) {
            CreditUserCardRelation creditUserCardRelation = creditUserCardRelationDao.findCreditUserCardRelationOne(creditCard.getUserId(),
                    card.getId());
            if (creditUserCardRelation != null) {
                card.setRepayment(creditUserCardRelation.getRepayment() == null ? new BigDecimal(0) : creditUserCardRelation.getRepayment());
                card.setUserCardRelation(creditUserCardRelation);
            } else {
                card.setRepayment(new BigDecimal(0));
            }

        }
        return card;
    }

    // @Override
    // public CreditCard findCreditCard(CreditCardBean creditCard) {
    // // TODO
    // // 查询还款金额
    // return creditCardDao.findOne(creditCard.getCardId());
    // }

    @Override
    public CreditCard findCreditCardByCardId(Long cardId, Long userId) {

        return creditCardDao.findCreditCardByCardId(cardId, userId);
    }

    /**
     * @Title: findCreditCardList
     * @Description:查询信用卡列表
     * @param creditCard
     * @return List<CreditCard> 返回类型 @throws
     */
    @Override
    public List<CreditCard> findCreditCardList(CreditCard creditCard) throws Exception {

        Sort sort = new Sort(Direction.ASC, "id");
        Specification<CreditCard> spec = getSpecification(creditCard);
        // 信用卡列表
        List<CreditCard> cardList = creditCardDao.findAll(spec, sort);
        if (CollectionUtils.isNotEmpty(cardList)) {
            for (CreditCard creditCard2 : cardList) {
                CreditUserCardRelation creditUserCardRelation = creditUserCardRelationDao.findCreditUserCardRelationOne(
                        creditCard.getUserId(), creditCard2.getId());
                creditCard2.setUserCardRelation(creditUserCardRelation);

                if (creditUserCardRelation.getRepayment() != null) {
                    creditCard2.setRepayment(creditUserCardRelation.getRepayment());
                } else {
                    creditCard2.setRepayment(new BigDecimal(0));
                }
            }
        }

        // // 信用卡和用户的关系
        // List<CreditUserCardRelation> relationList =
        // creditUserCardRelationDao.findList(creditCard.getUserId());
        // // 关系map
        // Map<Long, CreditUserCardRelation> relationMap = new HashMap<Long,
        // CreditUserCardRelation>();
        // relationList.forEach(relation -> {
        // relationMap.put(relation.getCardId(), relation);
        // });
        // cardList.forEach(card -> {
        // // 设置用户和卡的关系
        // card.setUserCardRelation(relationMap.get(card.getId()));
        // });
        return cardList;

        // return creditCardDao.findAll(spec, sort);
    }

    /**
     * @Title: findCreditCardListByDueDay @Description:根据账单日查卡 @param
     *         creditCard @return List<CreditCard> 返回类型 @throws
     */
    @Override
    public List<CreditCard> findCreditCardListByBillDay(CreditCard creditCard) throws Exception {
        int dueDay = Integer.parseInt(creditCard.getBillDay());
        return creditCardDao.findCreditCardListByBillDay(creditCard.getBillDay(), dueDay);
    }

    @Override
    public List<CreditCard> findCreditCardListByDueDay(Date dueDate) throws Exception {

        return creditCardDao.findCreditCardListByDueDay(dueDate);
    }

    private String getBankCode(CreditCard creditCard) {
        String bankCode = "";
        if (ClientBankCodeEnum.BOS.getCode().equals(creditCard.getBank().getCode())) {
            bankCode = CreditBankCodeEnum.SH.getCode();
        } else if (ClientBankCodeEnum.CGB.getCode().equals(creditCard.getBank().getCode())) {
            bankCode = CreditBankCodeEnum.GDB.getCode();
        } else if (ClientBankCodeEnum.CITI.getCode().equals(creditCard.getBank().getCode())) {
            bankCode = CreditBankCodeEnum.HQ.getCode();
        } else if (ClientBankCodeEnum.CNCB.getCode().equals(creditCard.getBank().getCode())) {
            bankCode = CreditBankCodeEnum.CITIC.getCode();
        } else {
            bankCode = creditCard.getBank().getCode();
        }
        return bankCode;
    }

    public Specification<CreditCard> getSpecification(CreditCard creditCard) {
        return new Specification<CreditCard>() {
            @Override
            public Predicate toPredicate(Root<CreditCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.hasText(creditCard.getEmail())) {
                    list.add(cb.equal(root.get("email").as(String.class), creditCard.getEmail()));
                }
                logger.info("userId:{}", creditCard.getUserId());
                if (creditCard.getUserId() != null && creditCard.getUserId() != 0) {
                    // list.add(cb.equal(root.get("userId").as(Long.class),
                    // creditCard.getUserId()));
                    Subquery<CreditUserCardRelation> subquery = query.subquery(CreditUserCardRelation.class);
                    Root<CreditUserCardRelation> subroot = subquery.from(CreditUserCardRelation.class);
                    subquery.select(subroot.get("id"));
                    Predicate userPredicate = cb.equal(subroot.get("userId").as(Long.class), creditCard.getUserId());
                    Predicate statusPredicate = cb.equal(subroot.get("status").as(Integer.class), 1);
                    Predicate joinPredicate = cb.equal(subroot.get("cardId").as(Long.class), root.get("id").as(Long.class));

                    subquery.where(userPredicate, statusPredicate, joinPredicate);
                    Predicate exists = cb.exists(subquery);
                    list.add(exists);
                }
                // // 按照账单日查询
                // if (StringUtils.hasText(creditCard.getBillDay())) {
                // cb.or(x, y)
                // cb.or(cb.and(cb.equal(root.get("billDay").as(String.class),
                // creditCard.getBillDay()).));
                // list.add(cb.equal(root.get("billDay").as(String.class),
                // creditCard.getBillDay()));
                // }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }

        };
    }

    private Specification<CreditCard> getSpecificationByUserId(CreditCard creditCard) {
        return new Specification<CreditCard>() {
            @Override
            public Predicate toPredicate(Root<CreditCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (creditCard.getId() != null) {
                    list.add(cb.equal(root.get("id").as(Long.class), creditCard.getId()));
                }
                if (creditCard.getUserId() != null) {
                    Subquery<CreditUserCardRelation> subquery = query.subquery(CreditUserCardRelation.class);
                    Root<CreditUserCardRelation> subRoot = subquery.from(CreditUserCardRelation.class);
                    subquery.select(subRoot.get("id"));
                    Predicate userPredicate = cb.equal(subRoot.get("userId").as(Long.class), creditCard.getUserId());
                    Predicate statusPredicate = cb.equal(subRoot.get("status").as(Integer.class), 1);
                    Predicate joinPredicate = cb.equal(subRoot.get("cardId").as(Long.class), root.get("id").as(Long.class));

                    subquery.where(userPredicate, statusPredicate, joinPredicate);
                    Predicate exists = cb.exists(subquery);
                    list.add(exists);

                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }

        };
    }

    /**
     * @Title: saveOrUpdateCreditCard
     * @Description:
     * @param creditCard
     * @return CreditCard 返回类型 @throws
     */
    @SuppressWarnings({ "unused", "unused" })
    @Transactional
    @Override
    public CreditCard saveOrUpdateCreditCard(CreditCard creditCard, CreditCardBean creditCardBean, CreditUserCardRelation relation)
            throws Exception {

        if (!StringUtils.hasText(creditCard.getCardholder())) {
            creditCard.setCardholder(null);
        }
        if (!StringUtils.hasText(creditCard.getName())) {
            creditCard.setName(null);
        }
        CreditCard oldCard = null;

        if (StringUtils.hasText(creditCard.getNumbers())) {
            String bankCode = getBankCode(creditCard);
            CreditBank bank = creditBankDao.findByCode(bankCode);
            String numbers = creditCardBean.getUserId() + "_" + creditCard.getNumbers().trim();
            oldCard = creditCardDao.findCreditCardBy(numbers.substring(numbers.length() - 4, numbers.length()), creditCard.getCardholder(),
                    bank.getId(), creditCardBean.getUserId());
            if (oldCard != null && creditCard.getId() == null) {
                if (oldCard.getStatus() == 0 || oldCard.getStatus() == 2) {

                } else {
                    throw new CreditClientException(CodeEnum.REPEAT);
                }
            }
            creditCard.setNumbers(numbers);
        }
        int year = 0;
        int month = 0;
        int day = 0;
        int dueDay = 0;

        LocalDate beginDate = null;
        LocalDate endDate = null;
        LocalDate dueDate = null;
        if (StringUtils.hasText(creditCard.getDueDay()) && StringUtils.hasText(creditCard.getBillDay())) {
            // 计算账单周期
            year = LocalDate.now().getYear();
            month = LocalDate.now().getMonthValue();
            day = LocalDate.now().getDayOfMonth();
            dueDay = Integer.parseInt(creditCard.getDueDay());
            int billDay = Integer.parseInt(creditCard.getBillDay());

            if (day > billDay) {
                // 过了账单日
                LocalDate billDate = LocalDate.of(year, month, billDay);
                // 开始日期
                beginDate = billDate.plusMonths(-1).plusDays(1);
                endDate = billDate.plusDays(0);

            } else {
                // 未过账单日
                LocalDate billDate = LocalDate.of(year, month, billDay);
                beginDate = billDate.plusMonths(-2).plusDays(1);
                endDate = billDate.plusMonths(-1);
            }
            // 固定还款日
            if (relation.getDueType() == 0) {
                dueDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), dueDay);
                // 如果账单日大于还款日，则下月还款
                if (dueDay < billDay) {
                    dueDate = dueDate.plusMonths(1);
                }

            } else {
                // 账单后
                dueDate = endDate.plusDays(relation.getDueDay());
            }
            // 周期
            creditCard.setBeginDate(DateUtil.localDateToDate(beginDate));
            creditCard.setEndDate(DateUtil.localDateToDate(endDate));

            creditCard.setStatus(StatusEnum.ENABLE.getStatus());
            // 还款日
            creditCard.setDueDate(DateUtil.localDateToDate(dueDate));
        }

        // 修改
        if ((oldCard != null && (oldCard.getStatus() == 1 || oldCard.getStatus() == 2))
                || (creditCard.getId() != null && creditCard.getId() != 0)) {

            // creditCardDao.updateCardNameById(creditCard.getName(),
            // creditCard.getId());
            logger.info("cardId:{}", creditCard.getId());
            if (oldCard == null) {
                oldCard = creditCardDao.findOne(creditCard.getId());
            }
            if (oldCard.getBillAmount() != null && creditCard.getCredits() != null
                    && creditCard.getCredits().subtract(oldCard.getBillAmount()).doubleValue() > 0) {
                logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】更新卡信息creditCard:{},oldCard:{}", creditCard, oldCard);

                // 手工添加需要判断银行
                if (oldCard.getSource() == 1 && creditCard.getBank() != null && StringUtils.hasText(creditCard.getBank().getCode())) {
                    String bankCode = getBankCode(creditCard);
                    CreditBank bank = creditBankDao.findByCode(bankCode);
                    // creditBillDao.
                    if (bank == null) {
                        throw new CreditClientException(CodeEnum.BANK_NOT_FOUND);
                    }
                    creditCard.setBank(bank);
                }
                oldCard.setUpdateDate(new Date());
                String number = creditCard.getNumbers();
                creditCard.setCompleteNumbers(null);
                creditCard.setNumbers(number);

                if (creditCard.getBillAmount() != null && creditCard.getBillAmount().doubleValue() < 0) {
                    creditCard.setBillAmount(null);
                }
                if (creditCard.getCredits() != null && creditCard.getCredits().doubleValue() < 0) {
                    creditCard.setCredits(null);
                }
                creditCard.setStatus(StatusEnum.ENABLE.getStatus());
                BeanUtil.copyPropertiesIgnoreNullCreditCard(creditCard, oldCard);
                creditCardDao.saveAndFlush(oldCard);
                CreditUserCardRelation oldRelation = null;
                if (creditCard.getId() != null) {
                    logger.info("creditCard.getId():{},creditCardBean.getUserId():{}", creditCard.getId(), creditCardBean.getUserId());
                    oldRelation = creditUserCardRelationDao.findCreditUserCardRelationOne(creditCardBean.getUserId(), creditCard.getId());
                } else {
                    logger.info("oldCard.getId():{},creditCardBean.getUserId():{}", oldCard.getId(), creditCardBean.getUserId());
                    oldRelation = creditUserCardRelationDao
                            .findCreditUserCardRelationOneUpdate(creditCardBean.getUserId(), oldCard.getId());
                }
                relation.setId(null);
                relation.setStatus(StatusEnum.ENABLE.getStatus());
                BeanUtil.copyPropertiesIgnoreNull(relation, oldRelation);

                creditUserCardRelationDao.saveAndFlush(oldRelation);
            } else {
                throw new CreditClientException(CodeEnum.CREDITS_FAIL);
            }

        } else {
            if ((creditCard.getBillAmount() != null && creditCard.getBillAmount().doubleValue() > 0)
                    || (creditCard.getCredits() != null && creditCard.getCredits().doubleValue() > 0)) {
                String bankCode = getBankCode(creditCard);
                CreditBank bank = creditBankDao.findByCode(bankCode);
                if (bank == null) {
                    throw new CreditClientException(CodeEnum.BANK_NOT_FOUND);
                }

                // 如果无默认-1
                if (creditCard.getBillAmount() != null) {

                    creditCard.setMinimum(new BigDecimal(creditCard.getBillAmount().doubleValue() * 0.1));
                } else {
                    creditCard.setBillAmount(new BigDecimal(-1));
                    creditCard.setMinimum(new BigDecimal(-1));
                }
                // 额度
                creditCard.setCredits(creditCard.getCredits() == null ? new BigDecimal(-1) : creditCard.getCredits());
                creditCard.setBank(bank);
                String number = creditCard.getNumbers().trim();
                creditCard.setCompleteNumbers(number.substring(number.length() - 4, number.length()));
                creditCard.setNumbers(number);
                // 创建时间
                creditCard.setCreateDate(new Date());
                // 用户id
                creditCard.setUserId(creditCardBean.getUserId());

                logger.info("creditCard=================:{}", creditCard);

                // // 根据唯一索引查询卡是否存在,如果存在更新,不存在保存
                // CreditCard card =
                // creditCardDao.findCreditCardBy(number.substring(number.length()
                // - 4, number.length()),
                // creditCard.getCardholder(), bank.getId(),
                // creditCardBean.getUserId());
                creditCardDao.saveAndFlush(creditCard);
                relation.setStatus(StatusEnum.ENABLE.getStatus());
                relation.setUserId(creditCardBean.getUserId());
                relation.setCardId(creditCard.getId());
                relation.setRepayment(new BigDecimal(0));
                creditUserCardRelationDao.saveAndFlush(relation);
                if (creditCard.getBillAmount() != null && creditCard.getBillAmount().doubleValue() > 0) {
                    CreditBill bill = new CreditBill();
                    bill.setCard(creditCard);
                    bill.setBeginDate(DateUtil.localDateToDate(beginDate));
                    bill.setEndDate(DateUtil.localDateToDate(endDate));
                    bill.setStatus(StatusEnum.ENABLE.getStatus());
                    bill.setYear(String.valueOf(endDate.getYear()));
                    bill.setMonth(String.valueOf(endDate.getMonthValue()));
                    bill.setDueDate(DateUtil.localDateToDate(dueDate));

                    // 账单金额
                    bill.setCurrentAmount(creditCard.getBillAmount());
                    creditBillDao.saveAndFlush(bill);

                    CreditUserBillRelation billRelation = new CreditUserBillRelation();
                    billRelation.setUserId(relation.getUserId());
                    billRelation.setBillId(bill.getId());
                    billRelation.setStatus(StatusEnum.ENABLE.getStatus());

                    creditUserBillRelationDao.saveAndFlush(billRelation);
                }
            }

        }
        return creditCard;
    }

    @Override
    public void updateCardNameById(String cardName, Long cardId) throws Exception {
        creditCardDao.updateCardNameById(cardName, cardId);
    }

    @Override
    public void updateCardRePayMent(Long cardId, Long userId, BigDecimal rePayMent) throws Exception {
        creditCardDao.updateCardRePayMent(cardId, userId, new Date(), rePayMent);
    }

    @Transactional
    @Override
    public void updateCardStatusById(Long cardId, Long userId) throws Exception {
        String uuid = UuidUtils.GetRandomString(16);
        CreditCard creditCard = creditCardDao.findOne(cardId);
        // 增加手工添加的账单需要修改卡号
        creditCardDao.updateCardNumbersById(uuid, cardId);

        creditUserCardRelationDao.updateCardStatusById(cardId, userId, new Date());

        // creditCardDao.updateCardStatusById(cardId, userId, new Date());

    }
}
