package com.pay.aile.bill.web;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.entity.CreditLoginLog;
import com.pay.aile.bill.entity.CreditUserEmailRelation;
import com.pay.aile.bill.enums.MailType;
import com.pay.aile.bill.enums.ResultEmun;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.job.CopyRelationRedisJobHandle;
import com.pay.aile.bill.job.DownloadMailScheduler;
import com.pay.aile.bill.job.RedisJobHandle;
import com.pay.aile.bill.service.CreditEmailService;
import com.pay.aile.bill.service.CreditLoginLogService;
import com.pay.aile.bill.service.impl.CreditUserEmailRelationServiceImpl;
import com.pay.aile.bill.service.mail.download.BaseMailOperation;
import com.pay.aile.bill.service.mail.download.DownloadMail;
import com.pay.aile.bill.service.mail.download.impl.MailCustomOperationImpl;
import com.pay.aile.bill.service.mail.relation.CreditFileRelation;
import com.pay.aile.bill.utils.MongoDownloadUtil;
import com.pay.aile.bill.utils.RedisLock;
import com.pay.aile.bill.utils.SpringContextUtil;

@Controller
public class EmailController {

    private static Logger logger = LoggerFactory.getLogger(EmailController.class);
    @Autowired
    public CreditEmailService creditEmailService;

    @Autowired
    public DownloadMail downloadMail;
    @Autowired
    private CreditFileRelation creditFileRelation;

    @Autowired
    private MongoDownloadUtil mongoDownloadUtil;
    @Autowired
    private RedisJobHandle redisJobHandle;
    @Autowired
    private CopyRelationRedisJobHandle copyRelationRedisJobHandle;
    @Autowired
    private CreditUserEmailRelationServiceImpl relationService;

    @Autowired
    private CreditLoginLogService creditLoginLogService;

    @RequestMapping(value = "/emailForm")
    public String emailForm(Model map, CreditEmail creditEmail) {

        return "emailForm";
    }

    @RequestMapping(value = "/emailList")
    public String emailList(Model map, CreditEmail creditEmail) {
        List emailList = creditEmailService.getEmailList(creditEmail);
        map.addAttribute("emailList", emailList);
        return "emailList";
    }

    @RequestMapping(value = "/getBill")
    public String getBill(CreditEmail creditEmail, String emailKey) {

        creditEmail.setEmail(creditEmail.getEmail() + "@" + emailKey);

        try {
            String pw = creditEmail.getPassword();
            creditEmail = creditEmailService.saveOrUpdate(creditEmail);
            // 添加email到任务队列中
            creditEmail.setPassword(pw);
            redisJobHandle.addJob(creditEmail);
            // downloadMail.execute(creditEmail);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "redirect:/emailList";
    }

    @RequestMapping(value = "/getEmailBill")
    @ResponseBody
    public JSONObject getEmailBill(CreditEmail creditEmail) {
        logger.info("需要导入的邮件：{}", JSONObject.toJSONString(creditEmail));
        String email = creditEmail.getEmail();
        try {
            // 进行邮箱登录判断是否可以登录
            String[] mailParms = StringUtils.split(creditEmail.getEmail(), "@");
            BaseMailOperation mailOperation = SpringContextUtil.getBean(MailType.getMailType(mailParms[1]).getClzz());

            try {
                if (!mailOperation.canLogin(creditEmail)) {
                    return ResultEmun.EMAIL_LOGIN_FAIL.getJsonMsg();
                }
            } catch (Exception e) {
                CreditLoginLog log = new CreditLoginLog();
                log.setEmail(email);
                log.setUserId(new Long(creditEmail.getUserId()));
                log.setCreateDate(new Date());
                creditLoginLogService.saveCreditLoginLog(log);
                return ResultEmun.EMAIL_LOGIN_FAIL.getJsonMsg();
            }
            if (RedisLock.getLock(email, email, 3)) {
                String pw = creditEmail.getPassword();
                String userId = creditEmail.getUserId();
                // 保存邮箱和用户的关系
                CreditUserEmailRelation relation = new CreditUserEmailRelation();
                // 根据邮箱名称查询邮箱
                List<CreditEmail> emailList = creditEmailService.getEmailList(creditEmail);
                logger.error("emailList======================{}", JSONObject.toJSONString(emailList));

                // 如果不存在此邮箱
                if (emailList == null || emailList.size() == 0) {
                    // logger.error("emailList.size()======================{}",
                    // emailList.size());

                    creditEmail = creditEmailService.saveOrUpdate(creditEmail);

                    logger.error("creditEmail======================{}", JSONObject.toJSONString(creditEmail));
                    // 添加email到任务队列中
                    creditEmail.setIsNew(true);
                    creditEmail.setDownload(true);
                    creditEmail.setPassword(pw);
                    creditEmail.setLastJobExecutionTime(1);
                    creditEmail.setLastJobTimestamp(
                            System.currentTimeMillis() - (DownloadMailScheduler.loopIntervalSeconds + 10) * 1000);

                    redisJobHandle.addJob(creditEmail);
                    relation.setEmailId(creditEmail.getId());
                } else {
                    logger.error("else=-================================================");
                    CreditEmail emails = emailList.get(0);
                    CreditUserEmailRelation old = relationService.findByUser(creditEmail.getUserId(), emails.getId());
                    if (old != null && old.getId() != null) {
                        // TODO 重复导入
                        creditEmail.setId(old.getEmailId());
                        creditEmailService.updateImport(creditEmail);
                        relation.setId(old.getId());
                    } else {
                        // 如果不存在user
                        CreditEmail oldCreditEmail = emailList.get(0);
                        oldCreditEmail.setUserId(userId);
                        oldCreditEmail.setId(oldCreditEmail.getId());
                        oldCreditEmail.setIsNew(true);
                        oldCreditEmail.setDownload(false);
                        oldCreditEmail.setPassword(pw);
                        oldCreditEmail.setLastJobExecutionTime(1);
                        oldCreditEmail.setLastJobTimestamp(
                                System.currentTimeMillis() - (DownloadMailScheduler.loopIntervalSeconds + 10) * 1000);

                        relation.setEmailId(oldCreditEmail.getId());
                        redisJobHandle.addJob(oldCreditEmail);
                        // copyRelationRedisJobHandle.addJob(oldCreditEmail);
                    }

                }
                logger.info("邮箱名：{}，密码：{}", creditEmail.getEmail(), creditEmail.getPassword());
                relation.setUserId(new Long(userId));

                relationService.saveOrUpdate(relation);
            }
        } catch (Exception e) {
            logger.error("邮箱:{}登录失败", creditEmail.getEmail(), e);
            return ResultEmun.EMAIL_LOGIN_FAIL.getJsonMsg();
        } finally {
            RedisLock.releaseLock(email, email);
        }

        return ResultEmun.SUCCESS.getJsonMsg();
    }

    @RequestMapping(value = "/getEmailDate")
    @ResponseBody
    public String getEmailDate(CreditEmail email) {

        return "emailList";
    }

    @RequestMapping(value = "/getFile")
    @ResponseBody
    public String getFile(String fileName) {
        try {
            return mongoDownloadUtil.getFile(fileName);
        } catch (MailBillException e) {
            return "";
        }

    }

    /**
     * @Title: getOtherEmailBill
     * @Description: 导入其他邮箱账单
     * @param creditEmail
     * @param relation
     * @return JSONObject 返回类型 @throws
     */
    @RequestMapping(value = "/getOtherEmailBill")
    @ResponseBody
    public JSONObject getOtherEmailBill(CreditEmail creditEmail, CreditUserEmailRelation relation) {
        String email = creditEmail.getEmail();
        try {
            logger.info("getOtherEmailBill==================={}", JSONObject.toJSONString(creditEmail));
            logger.info("getOtherEmailBill==================={}", JSONObject.toJSONString(relation));

            // 进行邮箱登录判断是否可以登录
            creditEmail.setEnableSsl(relation.getSsl());
            creditEmail.setHost(relation.getServer());
            creditEmail.setPort(String.valueOf(relation.getPort()));
            creditEmail.setProtocol(relation.getServer().contains("pop") ? "pop3" : "imap");
            MailCustomOperationImpl customOperation = SpringContextUtil.getBean(MailCustomOperationImpl.class);
            customOperation.setCreditEmail(creditEmail);
            logger.info("获取customOperation成功");
            if (!customOperation.canLogin(creditEmail)) {
                CreditLoginLog log = new CreditLoginLog();
                log.setEmail(email);
                log.setUserId(new Long(creditEmail.getUserId()));
                creditLoginLogService.saveCreditLoginLog(log);
                return ResultEmun.EMAIL_LOGIN_OR_SERVER_FAIL.getJsonMsg();
            }

            if (RedisLock.getLock(email, email, 3)) {
                String pw = creditEmail.getPassword();
                String userId = creditEmail.getUserId();
                // 保存邮箱和用户的关系
                // 根据邮箱名称查询邮箱
                List<CreditEmail> emailList = creditEmailService.getEmailList(creditEmail);
                logger.error("emailList======================{}", JSONObject.toJSONString(emailList));

                // 如果不存在此邮箱
                if (emailList == null || emailList.size() == 0) {
                    // logger.error("emailList.size()======================{}",
                    // emailList.size());

                    creditEmail = creditEmailService.saveOrUpdate(creditEmail);
                    // 添加email到任务队列中
                    creditEmail.setIsNew(true);
                    creditEmail.setDownload(true);
                    creditEmail.setPassword(pw);
                    creditEmail.setLastJobExecutionTime(1);
                    creditEmail.setLastJobTimestamp(System.currentTimeMillis() - 600 * 1000);
                    redisJobHandle.addJob(creditEmail);
                    relation.setEmailId(creditEmail.getId());
                } else {
                    logger.error("else=-================================================");
                    CreditEmail emails = emailList.get(0);
                    CreditUserEmailRelation old = relationService.findByUser(creditEmail.getUserId(), emails.getId());
                    if (old != null && old.getId() != null) {
                        // TODO 重复导入
                        // return ResultEmun.USER_REEOR.getJsonMsg();
                        creditEmail.setId(old.getEmailId());
                        creditEmailService.updateImport(creditEmail);
                        relation.setId(old.getId());
                    } else {
                        // 如果存在
                        CreditEmail oldCreditEmail = emailList.get(0);
                        oldCreditEmail.setUserId(userId);
                        oldCreditEmail.setId(oldCreditEmail.getId());
                        oldCreditEmail.setIsNew(false);
                        oldCreditEmail.setDownload(false);
                        oldCreditEmail.setPassword(pw);
                        oldCreditEmail.setLastJobExecutionTime(1);
                        oldCreditEmail.setLastJobTimestamp(System.currentTimeMillis() - 190L * 1000);
                        oldCreditEmail.setEnableSsl(creditEmail.getEnableSsl());
                        oldCreditEmail.setHost(creditEmail.getHost());
                        oldCreditEmail.setPort(creditEmail.getPort());
                        oldCreditEmail.setProtocol(creditEmail.getProtocol());
                        logger.info("oldCreditEmail=============================");
                        relation.setEmailId(oldCreditEmail.getId());
                        copyRelationRedisJobHandle.addJob(oldCreditEmail);
                    }

                }
                logger.info("邮箱名：{}，密码：{}", creditEmail.getEmail(), creditEmail.getPassword());
                /*
                 * relation.setUserId(new Long(userId));
                 * relation.setServer(creditEmail.getHost());
                 * relation.setPort(Integer.valueOf(creditEmail.getPort()));
                 * relation.setSsl(creditEmail.getEnableSsl());
                 */
                relationService.saveOrUpdate(relation);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResultEmun.EMAIL_LOGIN_FAIL.getJsonMsg();
        } finally {
            RedisLock.releaseLock(email, email);
        }

        return ResultEmun.SUCCESS.getJsonMsg();
    }

    @RequestMapping(value = "/showEmail")
    public String showEmail(CreditEmail email, Model model) {
        // List<EmailFile> fileList = new
        // view.addObject(attributeName, attributeValue)
        List<CreditFile> fileList = creditFileRelation.selectCreditFiles(email.getEmail());
        model.addAttribute("fileList", fileList);
        return "showEmail";
    }
}
