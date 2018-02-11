package com.pay.aile.bill.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.entity.CreditUserEmailRelation;
import com.pay.aile.bill.enums.CommonStatus;
import com.pay.aile.bill.enums.MailType;
import com.pay.aile.bill.job.FileQueueRedisHandle;
import com.pay.aile.bill.mapper.CreditBillMapper;
import com.pay.aile.bill.mapper.CreditCardMapper;
import com.pay.aile.bill.mapper.CreditEmailMapper;
import com.pay.aile.bill.mapper.CreditFileMapper;
import com.pay.aile.bill.mapper.CreditUserBillRelationMapper;
import com.pay.aile.bill.mapper.CreditUserCardRelationMapper;
import com.pay.aile.bill.mapper.CreditUserEmailRelationMapper;
import com.pay.aile.bill.service.CreditEmailService;
import com.pay.aile.bill.utils.JedisClusterUtils;
import com.pay.aile.bill.utils.MailProcessStatusCacheUtil;

@Service
public class CreditEmailServiceImpl implements CreditEmailService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CreditEmailMapper creditEmailMapper;

    @Autowired
    private CreditCardMapper creditCardMapper;

    @Autowired
    private CreditFileMapper creditFileMapper;

    @Autowired
    private CreditUserEmailRelationMapper userEmailRelationMapper;

    @Autowired
    private CreditUserCardRelationMapper userCardRelationMapper;

    @Autowired
    private CreditUserBillRelationMapper userBillRelationMapper;

    @Autowired
    private CreditBillMapper creditBillMapper;

    @Autowired
    private FileQueueRedisHandle fileQueueRedisHandle;

    @Override
    public List<CreditEmail> getCreditEmails() {
        List<CreditEmail> list = creditEmailMapper
                .selectList(new EntityWrapper<CreditEmail>().eq("status", CommonStatus.AVAILABLE.value));
        List<Long> customIds = new ArrayList<>();
        list.forEach(e -> {
            e.setPassword(decodePassword(e.getPassword()));
            String email = e.getEmail();
            String suffix = email.split("@")[1];
            if (MailType.getMailType(suffix) == null) {
                customIds.add(e.getId());
            }
        });
        if (!customIds.isEmpty()) {
            List<CreditUserEmailRelation> customEmails = userEmailRelationMapper
                    .selectList(new EntityWrapper<CreditUserEmailRelation>().in("email_id", customIds));
            Map<Long, Optional<CreditUserEmailRelation>> map = customEmails.stream()
                    .filter(item -> (item.getPort() != null && item.getServer() != null && item.getSsl() != null))
                    .collect(Collectors.groupingBy(CreditUserEmailRelation::getEmailId, Collectors.maxBy((o1, o2) -> {
                        return o1.getId() > o2.getId() ? 1 : -1;
                    })));
            list.stream().filter(item -> map.containsKey(item.getId())).forEach(e -> {
                CreditUserEmailRelation relation = map.get(e.getId()).get();
                e.setEnableSsl(relation.getSsl());
                e.setPort(String.valueOf(relation.getPort()));
                e.setHost(relation.getServer());
                e.setProtocol(relation.getServer().contains("pop") ? "pop3" : "imap");
            });
        }
        return list;
    }

    @Override
    public List<CreditEmail> getEmailList(CreditEmail email) {

        Wrapper<CreditEmail> wrapper = Condition.create();

        wrapper.addFilter(" STATUS = {0} ", CommonStatus.AVAILABLE.value);

        if (StringUtils.isNotEmpty(email.getEmail())) {
            String sqlWhere = " email like {0} ";
            wrapper.addFilter(sqlWhere, email.getEmail() + "%");

        }
        return creditEmailMapper.selectList(wrapper);

    }

    /**
     *
     * @Title: saveOrUpdate
     * @Description: 保存
     * @param email
     * @return CreditEmail 返回类型 @throws
     */
    @Override
    public CreditEmail saveOrUpdate(CreditEmail email) {

        CreditEmail emailParam = new CreditEmail();
        emailParam.setEmail(email.getEmail());
        emailParam = creditEmailMapper.selectOne(emailParam);
        if (!email.isEncrypt()) {
            email.setPassword(encodePassword(email.getPassword()));
        }
        if (emailParam == null) {
            creditEmailMapper.insert(email);
        } else {
            email.setId(emailParam.getId());
            creditEmailMapper.updateById(email);
        }

        return email;

    }

    @Override
    public void updateImport(CreditEmail email) {
        MailProcessStatusCacheUtil.initMailProcessStatus(email);

        // List<Map<String, Object>> newCardList =
        // userEmailRelationMapper.findNewCardList(email);
        // List<Long> cardIds = new ArrayList<>();
        // newCardList.forEach(card -> {
        // JSONObject cardJson = new JSONObject();
        // String completeNumbers = (String) card.get("completeNumbers");
        // // 获取需要展示的卡号
        // if (StringUtils.isNotEmpty(completeNumbers)) {
        // cardJson.put("cardNo",
        // completeNumbers.substring(completeNumbers.length() - 4,
        // completeNumbers.length()));
        // cardJson.put("cardholder", card.get("cardholder"));
        // cardJson.put("bankCode", card.get("bankCode"));
        // cardJson.put("bankName", card.get("bankName"));
        // cardJson.put("shortName", card.get("shortName"));
        // JedisClusterUtils.setSave(
        // Constant.REDIS_CARDS + email.getEmail() +
        // Constant.EMAIL_USERID_SEPARATOR + email.getUserId(),
        // cardJson.toJSONString());
        // cardIds.add((Long) card.get("id"));
        // }
        //
        // });
        // if (!cardIds.isEmpty()) {
        // CreditBill bill = new CreditBill();
        // bill.setNewStatus(1);
        // creditBillMapper.update(bill, new
        // EntityWrapper<CreditBill>().in("card_id", cardIds));
        // }
        // // 更新关联关系状态
        // // 更新邮箱关系
        // userEmailRelationMapper.updateStatus(email);
        // // 更新卡关系
        // userEmailRelationMapper.updateCardStatus(email);
        // // 更新账单关系
        // userEmailRelationMapper.updateBillStatus(email);
        // List<CreditFile> fileList =
        // creditFileMapper.findByRelation(email.getEmail());
        @SuppressWarnings("unchecked")
        Wrapper<CreditFile> wrapper = Condition.create();
        wrapper.addFilter(" email = {0} ", email.getEmail());

        List<CreditFile> fileList = creditFileMapper.selectList(wrapper);

        Wrapper<CreditCard> cardWrapper = Condition.create();
        cardWrapper.addFilter(" status = 1 and source=0 and user_id={0}", new Long(email.getUserId()));

        List<CreditCard> cardsList = creditCardMapper.selectList(cardWrapper);
        logger.debug("updateImport **** cardList={}", JSON.toJSONString(cardsList));

        List<String> numbers = new ArrayList<String>();
        List<String> bankCardNumbers = new ArrayList<String>();
        cardsList.forEach(card -> {
            String cardNo = card.getCompleteNumbers().substring(card.getCompleteNumbers().length() - 4,
                    card.getCompleteNumbers().length());
            numbers.add(cardNo);
            bankCardNumbers.add(card.getBankId() + card.getNumbers() + card.getCardholder());
        });
        logger.debug("updateImport *** numbers={}", numbers);
        String key = String.format(Constant.REDIS_EXISTS_CARD_NO, email.getEmail(), email.getUserId());
        String bankcardExistKey = String.format(Constant.REDIS_EXISTS_BANK_CARD, email.getEmail(), email.getUserId());
        JedisClusterUtils.delKey(key);
        JedisClusterUtils.delKey(bankcardExistKey);
        if (!numbers.isEmpty()) {
            JedisClusterUtils.saveToSet(key, numbers);
            JedisClusterUtils.saveToSet(bankcardExistKey, bankCardNumbers);
        }
        // JedisClusterUtils.saveString(key, JSONObject.toJSONString(numbers));
        fileList.forEach(file -> {
            file.setIsNew(true);
            file.setUserId(new Long(email.getUserId()));
            file.setEmail(email.getEmail());
            file.setEmailId(email.getId());
        });

        // 添加到任务队列
        if (fileList != null && fileList.size() > 0) {
            fileQueueRedisHandle.bathLeftPushFile(fileList);

        }

        // credit_card_analyzed_status_+email+userId
        // 待解析文件的数量
        JedisClusterUtils.saveString(
                Constant.REDIS_ANALYSIS_STATUS + email.getEmail() + Constant.EMAIL_USERID_SEPARATOR + email.getUserId(),
                String.valueOf(fileList.size()));
        // 郵件數量
        JedisClusterUtils.saveString(Constant.REDIS_EMAIL_NUMBER_STATUS + email.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + email.getUserId(), "100");
        JedisClusterUtils.saveString(Constant.REDIS_EMAIL_READ_NUMBER_STATUS + email.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + email.getUserId(), "100");
    }

    private String decodePassword(String password) {
        return new String(Base64.getDecoder().decode(password));
    }

    private String encodePassword(String password) {
        // 上SVN后使用公共加密工具
        /***
         * <groupId>com.lefu</groupId> <artifactId>commons-security</artifactId>
         * <version>1.0.31</version>
         */
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
}
