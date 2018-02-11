package com.pay.card.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.bean.CreditRePayMentBean;
import com.pay.card.dao.CreditBillDao;
import com.pay.card.dao.CreditCardDao;
import com.pay.card.dao.CreditRepaymentDao;
import com.pay.card.dao.CreditUserCardRelationDao;
import com.pay.card.dao.CreditUserInfoDao;
import com.pay.card.enums.RepaymentTypeEnum;
import com.pay.card.enums.StatusEnum;
import com.pay.card.model.CreditBill;
import com.pay.card.model.CreditCard;
import com.pay.card.model.CreditRepayment;
import com.pay.card.model.CreditUserBillRelation;
import com.pay.card.model.CreditUserInfo;
import com.pay.card.service.CreditRepaymentService;
import com.pay.card.utils.AmountUtil;
import com.pay.card.utils.DateUtil;

@Service
public class CreditRepaymentServiceImpl implements CreditRepaymentService {
    private static Logger logger = LoggerFactory.getLogger(CreditRepaymentServiceImpl.class);

    @Autowired
    private CreditRepaymentDao creditRepaymentDao;

    @Autowired
    private CreditBillDao creditBillDao;

    @Autowired
    private CreditCardDao creditCardDao;

    @Autowired
    private CreditUserInfoDao creditUserInfoDao;
    @Autowired
    private CreditUserCardRelationDao creditUserCardRelationDao;

    @Override
    @Transactional
    public CreditRepayment delete(CreditRepayment creditRepayment) {
        CreditRepayment repayment = null;
        if (creditRepayment.getId() != null) {
            repayment = creditRepaymentDao.findOne(creditRepayment.getId());
            // 增加记录是否已删除的判断
            if (repayment.getStatus().equals(StatusEnum.ENABLE.getStatus())) {
                repayment.setStatus(StatusEnum.DISENABLE.getStatus());

                creditRepaymentDao.saveAndFlush(repayment);
                // 更新card表repayment

                logger.info("updateCardRePayMent========={},{},{}", repayment.getCardId(), repayment.getUserInfo().getId(), new BigDecimal(
                        -repayment.getAmount().doubleValue()));
                creditCardDao.updateCardRePayMent(repayment.getCardId(), repayment.getUserInfo().getId(), new Date(), new BigDecimal(
                        -repayment.getAmount().doubleValue()));

                // 更新user_card_relation表repayment
                creditUserCardRelationDao.updateCardRepayment(repayment.getCardId(), repayment.getUserInfo().getId(), new Date(),
                        new BigDecimal(-repayment.getAmount().doubleValue()));
            }

        }

        return repayment;
    }

    @Override
    public List<JSONObject> findRePaymentDetail(CreditRepayment creditRepayment) throws Exception {
        List<JSONObject> resultList = new ArrayList<JSONObject>();
        // 还款明细
        List<CreditRepayment> listYearMonth = creditRepaymentDao.findRePaymentDetailYearMonth(creditRepayment.getCardId(), creditRepayment
                .getUserInfo().getId());

        List<CreditRepayment> list = creditRepaymentDao.findRePaymentDetail(creditRepayment.getCardId(), creditRepayment.getUserInfo()
                .getId());
        logger.info("cardid【" + creditRepayment.getCardId() + "】还款记录:{}", list);
        // 账单列表
        // TODO
        CreditCard creditCard = new CreditCard();
        // CreditBill creditBill = new CreditBill();
        // creditCard.setId(creditRepayment.getCardId());
        // creditBill.setCard(creditCard);
        // creditBill.setUserId(creditRepayment.getUserInfo().getId());
        // List<Order> orderList = new ArrayList<Sort.Order>();
        // Order order = new Order(Direction.DESC, "year");
        // Order order1 = new Order(Direction.DESC, "month");
        // orderList.add(order);
        // orderList.add(order1);
        // Sort sort = new Sort(orderList);
        // List<CreditBill> billList =
        // creditBillDao.findAll(getSpecification(creditBill), sort);

        creditCard = creditCardDao.findCreditCardByCardId(creditRepayment.getCardId(), creditRepayment.getUserInfo().getId());
        logger.info("cardid【" + creditRepayment.getCardId() + "】:{}", creditCard);
        getRepaymentJsonArray(resultList, list, creditCard, listYearMonth);

        return resultList;
    }

    @Override
    public List<CreditRepayment> findRepaymentList(CreditRepayment creditRepayment) throws Exception {
        Sort sort = new Sort(Direction.ASC, "id");
        return creditRepaymentDao.findAll(getSpecification(creditRepayment), sort);
    }

    private void getRepaymentJsonArray(List<JSONObject> resultList, List<CreditRepayment> list, CreditCard creditCard,
            List<CreditRepayment> listYearMonth) {

        for (CreditRepayment creditRepaymentYM : listYearMonth) {
            JSONObject json = new JSONObject();
            String remonth = creditRepaymentYM.getMonth();
            if (remonth.length() == 1) {
                remonth = "0" + remonth;
            }
            String date = creditRepaymentYM.getYear() + "-" + remonth + "-" + creditCard.getBillDay();
            date = DateUtil.getBillCycle(date);

            String month = creditRepaymentYM.getMonth();
            String year = creditRepaymentYM.getYear();
            json.put("billCycle", date);
            json.put("year", year);
            json.put("month", month);
            List<CreditRePayMentBean> paymentBeanList = new ArrayList<CreditRePayMentBean>();
            BigDecimal amount = new BigDecimal(0);
            for (CreditRepayment creditRepayment : list) {
                CreditRePayMentBean creditRePayMentBean = null;
                if (year.equals(creditRepayment.getYear()) && month.equals(creditRepayment.getMonth())) {
                    creditRePayMentBean = new CreditRePayMentBean();
                    creditRePayMentBean.setId(creditRepayment.getId());
                    creditRePayMentBean.setPaymentAamout(creditRepayment.getAmount() + "");
                    creditRePayMentBean.setPaymentDate(DateUtil.formatMMDD(creditRepayment.getCreateDate()));
                    creditRePayMentBean.setType(creditRepayment.getType() + "");
                    creditRePayMentBean.setTypeName(RepaymentTypeEnum.getMsg(creditRepayment.getType()));
                    amount = amount.add(creditRepayment.getAmount());
                    json.put("sumAmount", AmountUtil.amountFormat(amount));
                }
                if (creditRePayMentBean != null) {
                    paymentBeanList.add(creditRePayMentBean);
                }
                json.put("rePayMentList", paymentBeanList);
            }
            resultList.add(json);
        }

        // List<CreditRePayMentBean> paymentBeanList = new
        // ArrayList<CreditRePayMentBean>();
        // JSONObject jsonObject = new JSONObject();
        // for (CreditRepayment creditRepayment : list) {
        // BigDecimal amount = new BigDecimal(0);
        // String date = creditRepayment.getYear() + "-" +
        // creditRepayment.getMonth() + "-" + creditCard.getBillDay();
        // date = DateUtil.getBillCycle(date);
        //
        // CreditRePayMentBean creditRePayMentBean = null;
        // creditRePayMentBean = new CreditRePayMentBean();
        // creditRePayMentBean.setId(creditRepayment.getId());
        // creditRePayMentBean.setMonth(creditRepayment.getMonth());
        // creditRePayMentBean.setPaymentAamout(creditRepayment.getAmount() +
        // "");
        // creditRePayMentBean.setPaymentDate(DateUtil.formatMMDD(creditRepayment.getCreateDate()));
        // creditRePayMentBean.setType(creditRepayment.getType() + "");
        // creditRePayMentBean.setTypeName(RepaymentTypeEnum.getMsg(creditRepayment.getType()));
        // amount = amount.add(creditRepayment.getAmount());
        //
        // if (CollectionUtils.isNotEmpty(paymentBeanList)) {
        // for (CreditRepayment creditRepayment1 : list) {
        // if
        // (paymentBeanList.get(paymentBeanList.size()).getMonth().equals(creditRePayMentBean.getMonth()))
        // {
        // paymentBeanList.add(creditRePayMentBean);
        // }
        // }
        // } else {
        // paymentBeanList.add(creditRePayMentBean);
        // }
        //
        // jsonObject.put("sumAmount", amount);
        // jsonObject.put("rePayMentList", paymentBeanList);
        // jsonObject.put("billCycle", date);
        // jsonObject.put("year", creditRepayment.getYear());
        // jsonObject.put("month", creditRepayment.getMonth());
        // resultList.add(jsonObject);
        // }
        //
        // for (CreditBill creditBill : billList) {
        // JSONObject json = new JSONObject();
        // String date = creditBill.getYear() + "-" + creditBill.getMonth() +
        // "-" + creditBill.getCard().getBillDay();
        // date = DateUtil.getBillCycle(date);
        // // String beginDate = date.split("/")[0];
        // // String endDate = date.split("/")[1];
        //
        // String month = creditBill.getMonth();
        // json.put("billCycle", date);
        // json.put("year", creditBill.getYear());
        // json.put("month", month);
        // List<CreditRePayMentBean> paymentBeanList = new
        // ArrayList<CreditRePayMentBean>();
        // BigDecimal amount = new BigDecimal(0);
        // for (CreditRepayment creditRepayment : list) {
        // CreditRePayMentBean creditRePayMentBean = null;
        // if (month.equals(creditRepayment.getMonth() + "")) {
        // creditRePayMentBean = new CreditRePayMentBean();
        // creditRePayMentBean.setId(creditRepayment.getId());
        // creditRePayMentBean.setPaymentAamout(creditRepayment.getAmount() +
        // "");
        // creditRePayMentBean.setPaymentDate(DateUtil.formatMMDD(creditRepayment.getCreateDate()));
        // creditRePayMentBean.setType(creditRepayment.getType() + "");
        // creditRePayMentBean.setTypeName(RepaymentTypeEnum.getMsg(creditRepayment.getType()));
        // amount = amount.add(creditRepayment.getAmount());
        // json.put("sumAmount", amount);
        // }
        // if (creditRePayMentBean != null) {
        // paymentBeanList.add(creditRePayMentBean);
        // }
        // json.put("rePayMentList", paymentBeanList);
        // }
        // resultList.add(json);
        // }
        // logger.info("还款记录：{}", JsonUtils.toJsonString(resultList));

    }

    public Specification<CreditBill> getSpecification(CreditBill creditBill) {
        return new Specification<CreditBill>() {
            @Override
            public Predicate toPredicate(Root<CreditBill> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (creditBill.getCard() != null) {
                    list.add(cb.equal(root.get("card").get("id").as(Long.class), creditBill.getCard().getId()));
                }
                // 根据userid查询
                if (creditBill.getUserId() != null) {
                    Subquery<CreditUserBillRelation> subquery = query.subquery(CreditUserBillRelation.class);
                    Root<CreditUserBillRelation> subroot = subquery.from(CreditUserBillRelation.class);
                    subquery.select(subroot.get("id"));
                    Predicate userPredicate = cb.equal(subroot.get("userId").as(Long.class), creditBill.getUserId());
                    Predicate statusPredicate = cb.equal(subroot.get("status").as(Integer.class), 1);
                    Predicate joinPredicate = cb.equal(subroot.get("billId").as(Integer.class), root.get("id").as(Long.class));

                    subquery.where(userPredicate, statusPredicate, joinPredicate);
                    Predicate exists = cb.exists(subquery);
                    list.add(exists);
                }

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }

        };
    }

    private Specification<CreditRepayment> getSpecification(CreditRepayment creditRepayment) {
        return new Specification<CreditRepayment>() {
            @Override
            public Predicate toPredicate(Root<CreditRepayment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (creditRepayment.getUserInfo() != null) {
                    list.add(cb.equal(root.get("userInfo").get("id").as(Long.class), creditRepayment.getUserInfo().getId()));
                }
                if (creditRepayment.getBill() != null) {
                    Subquery<CreditUserBillRelation> subquery = query.subquery(CreditUserBillRelation.class);
                    Root<CreditUserBillRelation> subroot = subquery.from(CreditUserBillRelation.class);
                    subquery.select(subroot.get("id"));
                    Predicate statusPredicate = cb.equal(subroot.get("status").as(Integer.class), 1);
                    Predicate joinPredicate = cb.equal(subroot.get("billId").as(Long.class), root.get("bill").get("id").as(Long.class));
                    subquery.where(statusPredicate, joinPredicate);
                    Predicate exists = cb.exists(subquery);
                    list.add(exists);
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };

    }

    @Override
    @Transactional
    public CreditRepayment saveOrUpdate(CreditRepayment creditRepayment) throws Exception {
        if (creditRepayment.getBill() != null && creditRepayment.getBill().getId() != null) {
            CreditBill bill = creditBillDao.findOne(creditRepayment.getBill().getId());
            creditRepayment.setBill(bill);
        }
        // if (creditRepayment.getType() != null) {
        // if (creditRepayment.getType().intValue() == 1) {
        // CreditCard creditCard = creditCardDao.findCreditCardByCardId(creditRepayment.getCardId(), creditRepayment.getUserInfo()
        // .getId());
        // int amount = creditCard.getBillAmount().compareTo(creditCard.getRepayment());
        // if (amount > 0) {
        // creditRepayment.setAmount(new BigDecimal(amount));
        // }else {
        //
        // }
        // }
        // }

        CreditUserInfo userInfo = creditUserInfoDao.findOne(creditRepayment.getUserInfo().getId());
        creditRepayment.setUserInfo(userInfo);
        creditRepayment.setCreateDate(new Date());
        creditRepayment.setStatus(StatusEnum.ENABLE.getStatus());
        creditRepayment = creditRepaymentDao.saveAndFlush(creditRepayment);
        logger.info("{}", creditRepayment.getId());
        creditCardDao.updateCardRePayMent(creditRepayment.getCardId(), userInfo.getId(), new Date(), creditRepayment.getAmount());

        logger.info("update CardRepayment:cardId:{},userId:{},amount:{}", creditRepayment.getCardId(), userInfo.getId(),
                creditRepayment.getAmount());
        creditUserCardRelationDao.updateCardRepayment(creditRepayment.getCardId(), userInfo.getId(), new Date(),
                creditRepayment.getAmount());
        return creditRepayment;
    }
}
