package com.pay.card.dao.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.pay.card.dao.CreditBillDetailDao;
import com.pay.card.model.CreditBillDetail;

@Service
public class CreditBillDetailDaoImpl implements CreditBillDetailDao {

    private static Logger logger = LoggerFactory.getLogger(CreditBillDetailDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int findBillDatailCount(Long billId, String yearMonth) {
        int count = 0;
        String hql = "select count(*) from credit_bill_detail_" + yearMonth + " where bill_id = ?";
        List<Integer> list = jdbcTemplate.queryForList(hql, Integer.class, new Object[] { billId });
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return count;
    }

    @Override
    public List<CreditBillDetail> findBillDetailList(Long billId, Long userId, String yearMonth) {
        logger.info("账单id【" + billId + "】查询账单明细参数:{}", yearMonth);
        StringBuilder hql = new StringBuilder();
        hql.append(
                "select cbd.transaction_description as transactionDescription, cbd.transaction_amount as transactionAmount, cbd.transaction_date as transactionDate ")
                .append("from credit_bill_detail_" + yearMonth + " cbd")
                .append(" LEFT JOIN credit_bill_detail_relation_" + yearMonth
                        + " cbdr on cbd.id = cbdr.bill_detail_id ")
                .append("LEFT JOIN credit_user_bill_relation cubr on cubr.bill_id = cbdr.bill_id ")
                .append("where cubr.bill_id = " + billId).append(" and cubr.user_id = " + userId)
                .append(" and cubr.status = 1").append(" order by cbd.transaction_date desc");
        logger.info("账单id【" + billId + "】查询账单明细hql:{}", hql.toString());
        List<CreditBillDetail> list = jdbcTemplate.query(hql + "", new Object[] {},
                new BeanPropertyRowMapper<CreditBillDetail>(CreditBillDetail.class));
        return list;
    }

    @Override
    public int findFutureBillAmountCount(Long cardId, Long userId) {
        int count = 0;
        StringBuilder hql = new StringBuilder();
        // hql.append("SELECT coalesce(sum(cbd.transaction_amount),0)FROM
        // credit_bill_detail cbd ")
        // .append("LEFT JOIN credit_user_card_relation cucr on cbd.card_id =
        // cucr.card_id ")
        // .append("where cucr.user_id = " + userId + " and cucr.status = 1 and
        // cucr.card_id = " + cardId);
        hql.append("SELECT coalesce(sum(cbd.transaction_amount),0) FROM credit_bill_detail cbd ")
                .append("LEFT JOIN credit_user_card_relation cucr on cbd.card_id = cucr.card_id ")
                .append("LEFT JOIN credit_card cc on cc.id = cucr.card_Id ")
                .append("where cucr.user_id = " + userId + " and cucr.status = 1 and cucr.card_id = " + cardId
                        + " and cbd.transaction_date > cc.end_date");
        List<Integer> list = jdbcTemplate.queryForList(hql + "", Integer.class, new Object[] {});
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return count;
    }

    @Override
    public List<CreditBillDetail> findFutureBillDetail(Long cardId, Long userId) {
        // cucr.user_id = " + userId + " and
        StringBuilder hql = new StringBuilder();
        hql.append(
                "SELECT cbd.transaction_description as transactionDescription, cbd.transaction_amount as transactionAmount, cbd.transaction_date as transactionDate FROM credit_bill_detail cbd ")
                .append("LEFT JOIN credit_user_card_relation cucr on cbd.card_id = cucr.card_id ")
                .append("LEFT JOIN credit_card cc on cc.id = cucr.card_Id ")
                .append("where cucr.status = 1 and cucr.card_id = " + cardId
                        + " and cbd.transaction_date > cc.end_date ORDER BY cbd.transaction_date DESC");

        List<CreditBillDetail> list = jdbcTemplate.query(hql + "", new Object[] {},
                new BeanPropertyRowMapper<CreditBillDetail>(CreditBillDetail.class));
        return list;
    }
}
