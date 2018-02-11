package com.pay.card.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pay.card.bean.CreditCardBean;
import com.pay.card.dao.CreditBillDao;
import com.pay.card.dao.CreditUserBillRelationDao;
import com.pay.card.dao.CreditUserCardRelationDao;
import com.pay.card.dao.StatisticsDao;
import com.pay.card.model.CreditBill;
import com.pay.card.model.CreditEmail;
import com.pay.card.model.CreditUserBillRelation;
import com.pay.card.model.CreditUserCardRelation;
import com.pay.card.service.CreditBillService;
import com.pay.card.utils.AmountUtil;
import com.pay.card.utils.DateUtil;

@Service
public class CreditBillServiceImpl implements CreditBillService {

    private static Logger logger = LoggerFactory.getLogger(CreditBillServiceImpl.class);

    @Autowired
    private CreditBillDao creditBillDao;
    @Autowired
    private CreditUserBillRelationDao creditUserBillRelationDao;

    @Autowired
    private CreditUserCardRelationDao creditUserCardRelationDao;
    @Autowired
    private StatisticsDao statisticsDao;
    // 日期格式化
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy年");

    /**
     * 账单日历
     */
    @Override
    public JSONArray findBillCalendar(CreditCardBean creditCard) throws Exception {
        // 获取信用卡
        List<Map<String, Object>> cardList = statisticsDao.findCardList(creditCard);
        // 10天前的账单
        List<Map<String, Object>> billList = statisticsDao.findOldBillList(creditCard);
        // 用户和卡的关系表
        List<CreditUserCardRelation> relationList = creditUserCardRelationDao.findList(creditCard.getUserId());
        Map<Long, CreditUserCardRelation> relationMap = new HashMap<Long, CreditUserCardRelation>();
        relationList.forEach(relation -> {
            relationMap.put(relation.getCardId(), relation);
        });
        // logger.info("cardList====={}", JSONObject.toJSONString(cardList));
        // logger.info("billList====={}", JSONObject.toJSONString(billList));
        LocalDate maxDate = LocalDate.now().plusDays(40L);
        List<JSONObject> resultList = new ArrayList<JSONObject>();
        Map<Long, Map<String, Object>> cardMap = new HashMap<Long, Map<String, Object>>();

        // 未来的账单
        // cardList.forEach(card -> {
        for (Map<String, Object> card : cardList) {
            Long cardId = (Long) card.get("id");
            cardMap.put(cardId, card);
            // 账单日
            // int billDay = (Integer) card.get("billDay");
            // 还款日
            // int dueDay = (Integer) card.get("dueDay");
            if (card.get("billDay") != null) {
                getBillDeatil(resultList, maxDate, card, billList);

            }
            if (card.get("dueDate") != null) {
                getDueDeatil(resultList, maxDate, card, relationMap);
            }
        }

        // });
        // LocalDate today = LocalDate.now();
        // 今天的时间
        Date now = DateUtil.getCurrentDate0();
        // 前20天
        LocalDate minDate = LocalDate.now().plusDays(-20);
        // 历史账单
        getOldBillDeatil(resultList, billList, cardMap, DateUtil.localDateToDate(minDate));
        if (resultList == null || resultList.size() == 0) {
            return new JSONArray();
        }
        // 排序
        // resultList.sort((JSONObject bill1, JSONObject bill2) ->
        // (bill1.getDate("date").before(bill2.getDate("date"))
        // || bill1.getDate("date").equals(bill2.getDate("date"))) ? -1 : 1);

        resultList.sort((JSONObject bill1, JSONObject bill2) -> (bill1.getDate("date").equals(bill2.getDate("date")) ? 0
                : (bill1.getDate("date").before(bill2.getDate("date")) ? -1 : 1)));

        // 判断list中是否含有今天的对应的信息，如果增加null
        boolean todayFlag = false;
        for (int i = 0; i < resultList.size(); i++) {
            JSONObject tempDate = resultList.get(i);

            if (now.equals(tempDate.getDate("date"))) {
                todayFlag = true;
            }
            if (now.before(tempDate.getDate("date"))) {
                if (!todayFlag) {
                    todayFlag = true;
                    // 加入今天的空JSON
                    JSONObject todayJson = new JSONObject();
                    todayJson.put("date", now);
                    resultList.add(i, todayJson);
                }

            }
        }
        // 构造结果集
        Map<String, JSONObject> yearMap = new HashMap<String, JSONObject>();
        Map<String, JSONObject> dayMap = new HashMap<String, JSONObject>();

        JSONArray yearArray = new JSONArray();
        JSONArray newYearArray = new JSONArray();
        resultList.forEach(bill -> {
            Date date = bill.getDate("date");
            // 状态

            String billStatus = "now";
            if (now.before(date)) {
                billStatus = "later";
            } else if (now.after(date)) {
                billStatus = "past";
            }

            String yearStr = DateUtil.formatYYYY(date);
            String dateString = DateUtil.formatDate(date);
            if (yearMap.containsKey(yearStr)) {
                JSONObject yearJson = yearMap.get(yearStr);
                if (dayMap.containsKey(dateString)) {
                    JSONObject dayJson = dayMap.get(dateString);
                    JSONArray billArray = dayJson.getJSONArray("itemData");
                    // 增加账单的明细内容
                    if (bill.getString("bankName") != null) {
                        billArray.add(bill);
                    }

                    dayJson.put("itemData", billArray);

                } else {

                    // 天的array
                    JSONArray dayArray = yearJson.getJSONArray("dayData");
                    JSONObject dayJson = new JSONObject();
                    dayJson.put("billStatus", billStatus);
                    dayJson.put("date", DateUtil.formatMMDD3(date));
                    dayJson.put("week", DateUtil.getWeekOfDate(date));
                    // 账单数组
                    JSONArray billArray = new JSONArray();

                    if (bill.getString("bankName") != null) {
                        billArray.add(bill);
                    }
                    dayJson.put("itemData", billArray);

                    dayArray.add(dayJson);

                    yearJson.put("dayData", dayArray);

                    dayMap.put(dateString, dayJson);

                }
            } else {
                // 年的json
                JSONObject yearJson = new JSONObject();
                // 年
                yearJson.put("yearsName", yearStr);
                // 天的array
                JSONArray dayArray = new JSONArray();
                JSONObject dayJson = new JSONObject();
                dayJson.put("billStatus", billStatus);
                dayJson.put("date", DateUtil.formatMMDD3(date));
                dayJson.put("week", DateUtil.getWeekOfDate(date));
                // 账单数组
                JSONArray billArray = new JSONArray();

                if (bill.getString("bankName") != null) {
                    billArray.add(bill);
                }
                dayJson.put("itemData", billArray);

                dayArray.add(dayJson);

                yearJson.put("dayData", dayArray);

                dayMap.put(dateString, dayJson);
                yearMap.put(yearStr, yearJson);

                yearArray.add(yearJson);

            }

        });
//        while (maxDate.isAfter(minDate) || minDate.equals(minDate)) {
//            String minDateStr = minDate.format(dateTimeFormatter);
//            String yearStr = minDate.format(yearFormatter);
//            if (!dayMap.containsKey(minDateStr)) {
//
//            }
//
//            // 增加一天
//            minDate = minDate.plusDays(1);
//        }
        logger.info("yearArray====={}", JSONObject.toJSONString(yearArray));
        return yearArray;
    }

    /**
     * @Title: findCreditBillList
     * @Description: 查询账单列表
     * @param creditBill
     * @return List<CreditBill> 返回类型 @throws
     */
    @Override
    public List<CreditBill> findCreditBillList(CreditBill creditBill) throws Exception {

        List<Order> list = new ArrayList<Order>();
        Order order = new Order(Direction.DESC, "year");
        Order order2 = new Order(Direction.DESC, "month");
        list.add(order);
        list.add(order2);
        Sort sort = new Sort(list);
        return creditBillDao.findAll(getSpecification(creditBill), sort);
    }

    /**
     * @Title: findEmailByUser
     * @Description:根据用户查询邮箱
     * @param userId
     * @return List<CreditEmail> 返回类型 @throws
     */
    @Override
    public List<CreditEmail> findEmailByUser(Long userId) throws Exception {

        return statisticsDao.findEmailByUser(userId);
    }

    @Override
    public List<CreditBill> findNewCreditBillList(CreditBill creditBill) {
        List<Order> list = new ArrayList<Order>();
        Order order = new Order(Direction.DESC, "year");
        Order order2 = new Order(Direction.DESC, "month");
        list.add(order);
        list.add(order2);
        Sort sort = new Sort(list);
        Specification<CreditBill> specification = new Specification<CreditBill>() {
            @Override
            public Predicate toPredicate(Root<CreditBill> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (creditBill.getCard() != null) {
                    list.add(cb.equal(root.get("card").get("id").as(Long.class), creditBill.getCard().getId()));
                }
                list.add(cb.equal(root.get("newStatus").as(Integer.class), creditBill.getNewStatus()));
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }

        };
        return creditBillDao.findAll(specification, sort);
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

                    Predicate joinPredicate = cb.equal(subroot.get("billId").as(Integer.class),
                            root.get("id").as(Long.class));
                    if (creditBill.getNewStatus() != null) {
                        Predicate newStatusPredicate = cb.equal(subroot.get("newStatus").as(Integer.class), 1);
                        subquery.where(userPredicate, statusPredicate, newStatusPredicate, joinPredicate);
                    } else {
                        subquery.where(userPredicate, statusPredicate, joinPredicate);
                    }

                    Predicate exists = cb.exists(subquery);
                    list.add(exists);
                }

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }

        };
    }

    /**
     * @Title: saveOrUpdate
     * @Description: 保存账单
     * @param creditBill
     * @return CreditBill 返回类型 @throws
     */
    @Override
    public CreditBill saveOrUpdate(CreditBill creditBill) throws Exception {
        creditBill = creditBillDao.save(creditBill);
        return creditBill;
    }

    @Override
    public void updateBillStatusById(Long billId, Long userId) throws Exception {
        creditUserBillRelationDao.updateBillStatusById(billId, userId, new Date());
    }

    @Override
    public void updateNewStatus(List<Long> billId) {
        creditBillDao.updateNewStatus(billId, new Date());
    }

    private void getBillDeatil(List<JSONObject> resultList, LocalDate maxDate, Map<String, Object> card,
            List<Map<String, Object>> billList) {
        // 账单日
        String billDay = (String) card.get("billDay");
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        int day = LocalDate.now().getDayOfMonth();

        double credits = ((BigDecimal) card.get("credits")).doubleValue();

        Long id = (Long) card.get("id");
        LocalDate localBillDate = LocalDate.of(year, month, Integer.parseInt(billDay));
        int i = 0;
        // LocalDate localDueDate = DateUtil.dateToLocalDate(billDate);
        // 过了账单日
        if (day > Integer.parseInt(billDay)) {
            i = 1;
        } else if (day == Integer.parseInt(billDay)) {
            // 在账单日判断账单是否已经获取到
            for (Map<String, Object> bill : billList) {
                Long cardId = (Long) bill.get("cardId");

                if (cardId.longValue() == id.longValue()) {
                    Date billDates = (Date) bill.get("billDate");
                    LocalDate billLocalDate = DateUtil.dateToLocalDate(billDates);
                    if (localBillDate == billLocalDate) {
                        i = 1;
                        continue;
                    }

                }

            }
        }

        for (; i < 3; i++) {
            LocalDate nextDue = localBillDate.plusMonths(i);
            if (nextDue.isBefore(maxDate)) {
                JSONObject dayDetail = new JSONObject();
                // 名称
                dayDetail.put("bankName", card.get("cardName"));
                dayDetail.put("cardholder", card.get("cardholder"));
                if (credits > 0) {
                    // if (i == 0) {
                    // double billAmount = ((BigDecimal)
                    // card.get("billAmount")).doubleValue();
                    // if (billAmount > 0) {
                    // dayDetail.put("billNote", "剩余信用额度￥" + (credits -
                    // billAmount));
                    // } else {
                    // dayDetail.put("billNote", "剩余信用额度￥" +
                    // card.get("credits"));
                    // }
                    //
                    // }
                    dayDetail.put("billNote", "信用额度￥" + card.get("credits"));
                } else {
                    dayDetail.put("billNote", "");
                }

                // 卡号
                String numbers = (String) card.get("numbers");
                numbers = numbers.length() > 4 ? numbers.substring(numbers.length() - 4, numbers.length()) : numbers;
                dayDetail.put("bankNumber", numbers);
                // 银行code
                dayDetail.put("bankType", card.get("code"));
                // 类型
                dayDetail.put("billType", "账单日");

                // 时间
                dayDetail.put("date", DateUtil.localDateToDate(nextDue));

                resultList.add(dayDetail);
            }
        }
    }

    private void getDueDeatil(List<JSONObject> resultList, LocalDate maxDate, Map<String, Object> card,
            Map<Long, CreditUserCardRelation> relationMap) {
        Date dueDate = (Date) card.get("dueDate");
        LocalDate localDueDate = DateUtil.dateToLocalDate(dueDate);
        double credits = ((BigDecimal) card.get("credits")).doubleValue();

        Integer billDay = Integer.parseInt(String.valueOf(card.get("billDay")));
        // 信用卡来源
        Integer source = (Integer) card.get("source");
        LocalDate billDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), billDay);
        CreditUserCardRelation relation = relationMap.get(card.get("id"));
        for (int i = 1; i < 4; i++) {
            // 还款期

            LocalDate nextDue = null;
            if (source == 1 && relation != null) {
                if (relation.getDueType() == 0) {
                    // 固定还款日
                    nextDue = localDueDate.plusMonths(i);
                } else {
                    // 多少天后还款
                    nextDue = billDate.plusMonths(i).plusDays(relation.getDueDay());
                }
            } else {
                // 账单日
                Integer repayment_cycle = Integer.parseInt(String.valueOf(card.get("repayment_cycle")));
                // 固定还款日
                if (repayment_cycle == 0) {
                    nextDue = localDueDate.plusMonths(i);
                } else {
                    // 固定还款周期
                    nextDue = billDate.plusMonths(i).plusDays(repayment_cycle);
                }
            }

            // 判断是否在账单周期里
            if (nextDue.isBefore(maxDate)) {
                JSONObject dayDetail = new JSONObject();
                // 名称
                dayDetail.put("bankName", card.get("cardName"));
                // 卡号
                String numbers = (String) card.get("numbers");

                numbers = numbers.length() > 4 ? numbers.substring(numbers.length() - 4, numbers.length()) : numbers;
                dayDetail.put("bankNumber", numbers);
                dayDetail.put("cardholder", card.get("cardholder"));
                if (credits > 0) {
                    dayDetail.put("billNote", "信用额度￥" + card.get("credits"));

                } else {
                    dayDetail.put("billNote", "");

                }

                // 银行code
                dayDetail.put("bankType", card.get("code"));
                // 类型
                dayDetail.put("billType", "还款日");

                // 时间
                dayDetail.put("date", DateUtil.localDateToDate(nextDue));

                resultList.add(dayDetail);
            }
        }
    }

    private void getOldBillDeatil(List<JSONObject> resultList, List<Map<String, Object>> billList,
            Map<Long, Map<String, Object>> cardMap, Date minDate) {
        billList.forEach(bill -> {
            Map<String, Object> card = cardMap.get(bill.get("cardId"));
            if (card != null) {
                Date billDate = (Date) bill.get("billDate");
                Date dueDate = (Date) bill.get("dueDate");
                // 银行code
                String bankCode = (String) bill.get("bankCode");
                double repayment = ((BigDecimal) bill.get("repayment")).doubleValue();
                double minimum = ((BigDecimal) bill.get("minimum")).doubleValue();
                double credits = ((BigDecimal) bill.get("credits")).doubleValue();
                double amount = ((BigDecimal) bill.get("amount")).doubleValue();

                Date cardEnd = (Date) card.get("endDate");
                Date billEnd = (Date) bill.get("endDate");
                JSONObject dayBillDetail = new JSONObject();
                JSONObject dayDueDetail = new JSONObject();
                // 名称
                dayBillDetail.put("bankName", card.get("cardName"));
                dayDueDetail.put("bankName", card.get("cardName"));
                // 持卡人
                dayBillDetail.put("cardholder", card.get("cardholder"));
                dayDueDetail.put("cardholder", card.get("cardholder"));

                // 卡号
                String numbers = (String) card.get("numbers");
                numbers = numbers.length() > 4 ? numbers.substring(numbers.length() - 4, numbers.length()) : numbers;
                dayBillDetail.put("bankNumber", numbers);
                dayDueDetail.put("bankNumber", numbers);
                // 银行code
                dayBillDetail.put("bankType", card.get("code"));
                dayDueDetail.put("bankType", card.get("code"));
                // 类型
                dayBillDetail.put("billType", "账单日");
                dayDueDetail.put("billType", "还款日");
                // dayBillDetail.put("billNote", "剩余额度" + (credits - amount +
                // repayment));

                if (credits > 0) {
                    // 工商银行定制
                    if ("ICBC".equals(bankCode.toUpperCase())) {
                        LocalDate newBillEndDate = DateUtil.dateToLocalDate(billEnd).plusDays(1);
                        LocalDate newCardEnd = DateUtil.dateToLocalDate(cardEnd);
                        billDate = cardEnd;
                        if (newCardEnd.equals(newBillEndDate)) {
                            dayBillDetail.put("billNote", "剩余额度￥" + AmountUtil.amountFormat2(credits - amount));
                            dayDueDetail.put("billNote", "剩余额度￥" + AmountUtil.amountFormat2(credits - amount));
                        } else {
                            dayBillDetail.put("billNote", "信用额度￥" + credits);
                            dayDueDetail.put("billNote", "信用额度￥" + credits);
                        }
                    } else {
                        if (cardEnd.equals(billEnd)) {
                            dayBillDetail.put("billNote", "剩余额度￥" + AmountUtil.amountFormat2(credits - amount));
                            dayDueDetail.put("billNote", "剩余额度￥" + AmountUtil.amountFormat2(credits - amount));
                        } else {
                            dayBillDetail.put("billNote", "信用额度￥" + credits);
                            dayDueDetail.put("billNote", "信用额度￥" + credits);
                        }

                    }

                }

                // if (repayment >= amount) {
                //
                // dayDueDetail.put("billNote", "已还清");
                // } else if (repayment >= minimum) {
                // dayDueDetail.put("billNote", "需还款￥" + (amount - repayment) +
                // "，已还最低");
                // } else {
                // dayDueDetail.put("billNote", "需还款￥" + (amount - repayment));
                //
                // }
                // 时间
                dayBillDetail.put("date", billDate);
                dayDueDetail.put("date", dueDate);

                // 添加到结果接种
                if (billDate != null && minDate.before(billDate)) {
                    resultList.add(dayBillDetail);

                }
                if (dueDate != null && minDate.before(dueDate)) {
                    resultList.add(dayDueDetail);

                }
            }

        });

    }

}
