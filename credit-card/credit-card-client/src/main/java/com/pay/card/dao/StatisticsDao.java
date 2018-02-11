package com.pay.card.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.pay.card.Constants;
import com.pay.card.bean.CreditCardBean;
import com.pay.card.model.CreditEmail;
import com.pay.card.model.CreditSet;
import com.pay.card.utils.DateUtil;

@Repository
public class StatisticsDao {
    private static Logger logger = LoggerFactory.getLogger(StatisticsDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @Title: findBillDayCard
     * @Description: 查询到账单日的卡
     * @param creditCard
     * @return List<Map<String,Object>> 返回类型 @throws
     */
    public List<Map<String, Object>> findBillDayCard(String maxId) {

        StringBuffer sql = new StringBuffer(
                "SELECT k.short_name,c.complete_numbers as numbers,u.customer_no,u.id,c.id as cardId,s.times");
        sql.append(" FROM credit_card c ");
        sql.append(" INNER JOIN credit_user_card_relation uc on uc.card_Id = c.id ");
        sql.append(" INNER JOIN credit_user_info u on u.id = uc.user_Id ");
        sql.append(" INNER JOIN credit_bank k ON k.id = c.bank_id ");
        sql.append(" INNER JOIN credit_set s on s.user_id = u.id");
        sql.append(" where u.customer_no is not NULL and c.bill_day=" + DateUtil.getCurrentDay()
                + " and s.billday_reminder=1 and uc.status=1 and c.status=1 ");

        if (StringUtils.hasText(maxId)) {
            sql.append(" and c.id>").append(maxId);
        }

        return jdbcTemplate.queryForList(sql.toString());
    }

    public List<Map<String, Object>> findBillList(CreditCardBean creditCard) {
        StringBuffer sql = new StringBuffer("select b.card_id as cardId from credit_bill b where ");
        sql.append(creditCard.getUserId());
        sql.append(") and (b.end_date>='");
        sql.append(DateUtil.plusDays(-10));
        sql.append("' or b.due_date >= '");
        sql.append(DateUtil.plusDays(-10));
        sql.append("')");
        return jdbcTemplate.queryForList(sql.toString());
    }

    /**
     * @Title: findCardList
     * @Description:查询用户的信用卡列表
     * @param creditCard
     * @return List<Map<String,Object>> 返回类型 @throws
     */
    public List<Map<String, Object>> findCardList(CreditCardBean creditCard) {

        String sql = "select c.id,b.id as bankId,b.short_name as cardName,b.code as code,c.complete_numbers as numbers,c.bill_day as billDay,c.due_day as dueDay,c.due_date as dueDate,b.repayment_cycle,c.source,ifnull(c.name,c.cardholder) as cardholder,c.credits,bill_amount as billAmount,c.end_date as endDate from credit_card c left join credit_bank b on b.id = c.bank_id where EXISTS (select cr.id from credit_user_card_relation cr where cr.status=1 and cr.card_id = c.id and cr.user_id ="

                + creditCard.getUserId() + ")";
        return jdbcTemplate.queryForList(sql.toString());
    }

    public List<Map<String, Object>> findCmbSimple(String email) {
        String sql = "select f.id from credit_file f where f.email='" + email + "' and f.cmb_simple=1";

        return jdbcTemplate.queryForList(sql);

    }

    /**
     * 查询还款状态
     *
     * @param page
     * @param num
     * @return
     */
    public List<Map<String, Object>> findCreditBillStatusList(Integer page, Integer num) {
        Integer pend = (page - 1) * num;
        StringBuffer sql = new StringBuffer(
                "select cui.id userId,cb.id billId,cui.customer_no customerNo,cb.year year,cb.month month,cb.current_amount<=cb.repayment isPayOff from credit_bill cb ");
        sql.append("left join credit_user_bill_relation cubr on cb.id=cubr.bill_Id ");
        sql.append("left join credit_user_info cui on cubr.user_Id=cui.id ");
        sql.append(" where cubr.status=1 ");
        sql.append("limit ").append(pend).append(",").append(num);
        return jdbcTemplate.queryForList(sql.toString());
    }

    /**
     * 分页查询账单解析记录
     */
    public List<Map<String, Object>> findCreditFileList(Integer page) {
        StringBuffer sql = new StringBuffer(
                "SELECT cf.process_result reuslt,DATE_FORMAT(cf.create_date,'%Y-%m-%d %H:%i:%s') createDate,ur.user_id userId");
        sql.append(",ui.customer_no customerNo,cb.id as billId,cb.foreign_currency foreignCurrency");
        sql.append(" FROM credit_file cf");
        sql.append(" LEFT JOIN credit_user_file_relation ur ON ur.file_id=cf.id");
        sql.append(" LEFT JOIN credit_user_info ui ON ui.id = ur.user_id");
        sql.append(" LEFT JOIN credit_bill cb ON cb.file_id = cf.id");
        sql.append(" WHERE 1=1 LIMIT ?,?");

        Integer pend = page * Constants.PAGE_SIZE;
        Integer pstart = pend - Constants.PAGE_SIZE;
        return jdbcTemplate.queryForList(sql.toString(), pstart, pend);
    }

    /**
     * 查询解析失败的账单文件
     */
    public List<Map<String, Object>> findCreditFileList(String email, Date startTime, Date endTime, Integer page) {
        List<Object> param = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer(
                "SELECT cf.file_name fileName,DATE_FORMAT(cf.create_date,'%Y-%m-%d %H:%i:%s') createDate,");
        sql.append(" cf.subject subject,cf.email email,cf.mail_type type,MONTH(cf.sent_date) month");
        sql.append(" FROM credit_file cf");
        sql.append(" WHERE cf.process_result=-1 and cf.create_date >= ?");
        param.add(startTime);
        if (StringUtils.hasText(email)) {
            sql.append(" and cf.email = ?");
            param.add(email);
        }
        if (endTime != null) {
            sql.append(" and cf.create_date <= ?");
            param.add(endTime);
        }
        sql.append(" LIMIT ?,?");
        Integer pend = page * 100;
        Integer pstart = pend - 100;
        param.add(pstart);
        param.add(pend);

        return jdbcTemplate.queryForList(sql.toString(), param.toArray());
    }

    /**
     * 查询信用卡信息
     *
     * @param page
     * @param num
     * @return
     */
    public List<Map<String, Object>> findCreditInfoList(Integer page, Integer num) {
        Integer pend = (page - 1) * num;
        StringBuffer sql = new StringBuffer(
                "select cui.id userId,cc.id cardId,cui.customer_no customerNo,cc.complete_numbers cardNo,cb.name name,");
        sql.append("DATE_FORMAT(cc.create_date,'%Y-%m-%d %H:%i:%s') createDate,cc.source as source, ");
        sql.append("DATE_FORMAT(if(cc.status>0,null,cc.update_date),'%Y-%m-%d %H:%i:%s') deleteDate,cc.status status ");
        sql.append("from credit_card cc left join credit_user_card_relation cucr on cc.id=cucr.card_Id ");
        sql.append("left join credit_user_info cui on cucr.user_Id=cui.id ");
        sql.append("left join credit_bank cb on cc.bank_id= cb.id where cucr.status=1 ");
        sql.append("limit ").append(pend).append(",").append(num);

        return jdbcTemplate.queryForList(sql.toString());
    }

    /**
     * 分页查询邮箱登录失败记录
     */
    public List<Map<String, Object>> findCreditLoginLog(Integer page) {
        StringBuffer sql = new StringBuffer(
                "SELECT lg.user_id userId,lg.customer_no customerNo,lg.email email,lg.domain domain");
        sql.append(",DATE_FORMAT(lg.create_date,'%Y-%m-%d %H:%i:%s') createDate,lg.reason reason");
        sql.append(" FROM credit_login_log lg");
        sql.append(" WHERE 1=1 LIMIT ?,?");

        Integer pend = page * Constants.PAGE_SIZE;
        Integer pstart = pend - Constants.PAGE_SIZE;
        return jdbcTemplate.queryForList(sql.toString(), pstart, pend);
    }

    /**
     * 分页查询提醒设置
     *
     * @param creditSet
     * @param page
     * @return
     */
    public List<Map<String, Object>> findCreditSetList(CreditSet creditSet, Integer page) {
        StringBuffer sql = new StringBuffer("SELECT cs.user_id userId,cs.billday_reminder billdayReminder,");
        sql.append("cs.out_bill_reminder outBillReminder,cs.repayment_reminder repaymentReminder,");
        sql.append("cs.overdue_reminder overdueReminder,cui.customer_no customerNo");
        sql.append(" FROM credit_set cs LEFT JOIN credit_user_info cui on cui.id=cs.user_id");
        sql.append(" WHERE 1=1 LIMIT ?,?");

        Integer pend = page * Constants.PAGE_SIZE;
        Integer pstart = pend - Constants.PAGE_SIZE;
        return jdbcTemplate.queryForList(sql.toString(), pstart, pend);
    }

    /**
     * 查询所有设置还款提醒且今天为还款日的
     *
     * @param maxId
     * @return
     */
    public List<Map<String, Object>> findDueDayBillList(String maxId) {

        StringBuffer sql = new StringBuffer(
                "SELECT k.short_name,c.complete_numbers as numbers,u.customer_no,s.advance_day,c.id as cardId,s.times FROM ");
        sql.append(" credit_card c   ");
        sql.append(" INNER JOIN credit_bank k ON k.id = c.bank_id  ");
        sql.append(" INNER JOIN credit_user_card_relation uc on uc.card_Id = c.id  ");
        sql.append(" INNER JOIN credit_user_info u on u.id = uc.user_Id  ");
        sql.append(" INNER JOIN credit_set s on s.user_id = u.id  ");
        sql.append(" where s.repayment_reminder=1 and c.status=1 ");
        sql.append(" and u.customer_no is not NULL and TIMESTAMPDIFF(DAY,c.due_date,'");
        sql.append(DateUtil.getCurrentDate());
        sql.append(" 00:00:00')=0 ");
        // sql.append("and s.times=" +DateUtil.getCurrentHour());

        if (StringUtils.hasText(maxId)) {
            sql.append(" and c.id>").append(maxId);
        }
        logger.info("还款日当日提醒sql:{}", sql.toString());
        return jdbcTemplate.queryForList(sql.toString());
    }

    /**
     * @Title: findEmailById
     * @Description: 根据id查询邮箱
     * @param emailId
     * @return CreditEmail 返回类型 @throws
     */
    public CreditEmail findEmailById(Long emailId) {
        String sql = "select id,email,`password` as password from credit_email where id=" + emailId;
        return jdbcTemplate.queryForObject(sql, CreditEmail.class);
    }

    /**
     * @Title: findEmailByUser
     * @Description:根据用户id查询邮箱
     * @param userId
     * @return List<CreditEmail> 返回类型 @throws
     */
    public List<CreditEmail> findEmailByUser(Long userId) {

        String sql = "select e.id,e.email,e.`password` as password from credit_email e inner join credit_user_email_relation er on e.id = er.email_id where er.`status`=1 and er.user_id="
                + userId;
        List<CreditEmail> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<CreditEmail>(CreditEmail.class));
        return list;
    }

    public long findMaxBillId() {

        String sql = "select ifnull(max(id),0) from credit_bill";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long findMaxCardId() {

        String sql = "select ifnull(max(id),0) from credit_card";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /**
     * @Title: findOldBillList
     * @Description:查询历史账单
     * @param creditCard
     * @return List<Map <String,Object>> 返回类型 @throws
     */
    public List<Map<String, Object>> findOldBillList(CreditCardBean creditCard) {
        StringBuffer sql = new StringBuffer(
                "select b.card_id as cardId,b.end_date as billDate,b.due_date as dueDate,ifnull(b.repayment,0) as repayment, ifnull(b.minimum,0) minimum,ifnull(b.credits,0) as credits,ifnull(b.current_amount,0) as amount,ifnull(c.name,c.cardholder) as cardholder,b.end_date as endDate   from credit_bill b inner join credit_card c on c.id= b.card_id where EXISTS (select cr.id from credit_user_card_relation cr where cr.status=1 and cr.card_id = c.id and cr.user_id =");
        sql.append(creditCard.getUserId());
        sql.append(
                " ) and exists (select ub.id from credit_user_bill_relation ub where ub.bill_Id=b.id and ub.status = 1 and ub.user_id= ");
        sql.append(creditCard.getUserId());
        sql.append(") and (b.end_date>='");
        sql.append(DateUtil.plusDays(-10));
        sql.append("' or b.due_date >= '");
        sql.append(DateUtil.plusDays(-10));
        sql.append("')");
        return jdbcTemplate.queryForList(sql.toString());
    }

    /**
     * @Title: findOutBillCard
     * @Description: 已出账单列表
     * @param creditCard
     * @return List<Map<String,Object>> 返回类型 @throws
     */
    public List<Map<String, Object>> findOutBillCard(String maxId) {

        StringBuffer sql = new StringBuffer(
                "SELECT k.short_name,c.complete_numbers as numbers,u.customer_no,c.id as cardId,s.times FROM credit_bill b ");
        sql.append(" INNER JOIN credit_card c ON b.card_id = c.id  ");
        sql.append(" INNER JOIN credit_bank k ON k.id = c.bank_id  ");
        sql.append(" INNER JOIN credit_user_card_relation uc on uc.card_Id = c.id  ");
        sql.append(" INNER JOIN credit_user_info u on u.id = uc.user_Id  ");
        sql.append(" INNER JOIN credit_set s on s.user_id = u.id  ");
        sql.append(" where u.customer_no is not NULL and  b.end_date=' ");
        sql.append(DateUtil.getCurrentDate());
        sql.append(" 00:00:00' and s.out_bill_reminder=1 and c.status=1 and uc.status=1 ");
        if (StringUtils.hasText(maxId)) {
            sql.append(" and b.id>").append(maxId);
        }
        return jdbcTemplate.queryForList(sql.toString());
    }

    //
    // public CreditEmail updateEmailUser(Long userId, Long emailId) {
    // String sql = "select id,email,`password` as password from credit_email
    // where id=" + emailId;
    // return jdbcTemplate.queryForObject(sql, CreditEmail.class);
    // }

    public List<Map<String, Object>> findOverdueBillList(String maxId) {

        StringBuffer sql = new StringBuffer(
                "SELECT k.short_name,c.complete_numbers as numbers,u.customer_no,c.id as cardId,s.times FROM credit_bill b ");
        sql.append(" INNER JOIN credit_card c ON b.card_id = c.id  ");
        sql.append(" INNER JOIN credit_bank k ON k.id = c.bank_id  ");
        sql.append(" INNER JOIN credit_user_card_relation uc on uc.card_Id = c.id  ");
        sql.append(" INNER JOIN credit_user_info u on u.id = uc.user_Id  ");
        sql.append(" INNER JOIN credit_set s on s.user_id = u.id  ");
        sql.append(" where u.customer_no is not NULL and c.status=1 and uc.status=1 and  b.due_date=' ");
        sql.append(DateUtil.getBeforeDate());
        sql.append(" 00:00:00' and s.overdue_reminder=1 ");
        if (StringUtils.hasText(maxId)) {
            sql.append(" and b.id>").append(maxId);
        }
        // logger.info(sql.toString());
        return jdbcTemplate.queryForList(sql.toString());
    }

    public List<Map<String, Object>> findRepaymentBillList(String maxId) {

        StringBuffer sql = new StringBuffer(
                "SELECT k.short_name,c.complete_numbers as numbers,u.customer_no,s.advance_day,c.id as cardId,s.times FROM ");
        sql.append(" credit_card c   ");
        sql.append(" INNER JOIN credit_bank k ON k.id = c.bank_id  ");
        sql.append(" INNER JOIN credit_user_card_relation uc on uc.card_Id = c.id  ");
        sql.append(" INNER JOIN credit_user_info u on u.id = uc.user_Id  ");
        sql.append(" INNER JOIN credit_set s on s.user_id = u.id  ");
        sql.append(" where s.repayment_reminder=1 and c.status=1 and uc.status=1 and s.advance_day>0");
        sql.append(" and u.customer_no is not NULL and TIMESTAMPDIFF(DAY,c.due_date,'");
        sql.append(DateUtil.getCurrentDate());
        sql.append(" 00:00:00')=s.advance_day ");
        // sql.append("and s.times=" +DateUtil.getCurrentHour());

        if (StringUtils.hasText(maxId)) {
            sql.append(" and c.id>").append(maxId);
        }
        logger.info(sql.toString());
        return jdbcTemplate.queryForList(sql.toString());
    }

    public void updateUserCardStatus(Long userId, String email) {
        String sql = " update credit_user_card_relation  set `status`=0 where user_id=" + userId
                + " and EXISTS (select cc.id from credit_card cc where cc.id = card_id and cc.email='" + email + "'";

        jdbcTemplate.execute(sql);

    }

    public void updateUserEmailStatus(Long userId, Long emailId) {
        String sql = "update credit_user_email_relation set `status`=0 where user_id=" + userId + " and email_id="
                + emailId;
        jdbcTemplate.execute(sql);

    }

}
