package com.pay.aile.bill.analyze.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.aile.bill.analyze.BankMailAnalyzer;
import com.pay.aile.bill.analyze.IParseMail;
import com.pay.aile.bill.config.TemplateCache;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.EmailFile;
import com.pay.aile.bill.entity.SendMail;
import com.pay.aile.bill.enums.BankCodeEnum;
import com.pay.aile.bill.exception.AnalyzeBillException;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.model.CreditFileModel;
import com.pay.aile.bill.service.CreditFileService;
import com.pay.aile.bill.utils.JedisClusterUtils;
import com.pay.aile.bill.utils.MailSendUtil;
import com.pay.aile.bill.utils.MongoDownloadUtil;
import com.pay.aile.bill.utils.SpringContextUtil;

/**
 *
 * @author Charlie
 * @description
 */
@Service("parseMail")
public class ParseMailImpl implements IParseMail {
    @Resource
    private CreditFileService creditFileService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MongoDownloadUtil mongoDownloadUtil;

    @Autowired
    private MailSendUtil mailSendUtil;

    /**
     * 提取后的邮件内容解析
     */
    @Autowired
    private List<BankMailAnalyzer> parsers;

    @Override
    public void execute() {
        List<CreditFileModel> fileList = creditFileService.findUnAnalyzedList();
        if (fileList == null || fileList.isEmpty()) {
            logger.info("未解析邮件账单为空");
            return;
        }
        executeParseFileList(fileList);
    }

    @Override
    public void execute(CreditEmail creditEmail) {
        List<CreditFileModel> fileList = creditFileService.findUnAnalyzedListByEmail(creditEmail);
        if (fileList == null || fileList.isEmpty()) {
            logger.info("未解析邮件账单为空");
            return;
        }
        executeParseFileList(fileList);

    }

    /**
     *
     * @Title: executeParseFile
     * @Description:解析单个文件
     * @param creditFile
     * @return void 返回类型 @throws
     */
    @Override
    public void executeParseFile(CreditFileModel creditFile) {
        // 解析
        Exception error = null;
        Long id = creditFile.getId();
        AnalyzeParamsModel apm = new AnalyzeParamsModel();
        try {
            apm = setModel(apm, creditFile);
            try {
                BankMailAnalyzer parser = null;
                for (BankMailAnalyzer p : parsers) {
                    if (p.support(apm.getBankCode())) {
                        parser = p;
                        break;
                    }
                }
                if (parser == null) {
                    throw new RuntimeException(
                            String.format("no parsers found,bankCode=%s,email=%s", apm.getBankCode(), apm.getEmail()));
                }
                parser.analyze(apm);
            } catch (Exception e) {
                error = e;
                logger.error("文件解析错误!" + e.getMessage(), e);
                // sendMail();
            }
            if (error == null) {
                // 更新解析状态
                creditFileService.updateProcessResult(1, id);
            } else {
                creditFileService.updateProcessResult(-1, id);
            }
        } catch (Exception e) {
            // 更新解析状态
            creditFileService.updateProcessResult(-1, id);

            logger.error(e.getMessage(), e);
            error = e;
        } finally {
            apm.setError(error);
            // logger.info("publishAnalyzeStatusEvent================================{}",
            // JSONObject.toJSONString(apm));
            SpringContextUtil.publishAnalyzeStatusEvent(apm);

        }

    }

    /**
     *
     * @Title: executeParseFileList
     * @Description:批量解析文件
     * @param fileList
     * @return void 返回类型 @throws
     */
    public void executeParseFileList(List<CreditFileModel> fileList) {
        fileList.forEach(creditFile -> {
            try {
                executeParseFile(creditFile);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    /**
     *
     * @Title: setFileContent
     * @Description: 设置邮件需要解析的内容
     * @param creditFile
     * @param apm
     * @return void 返回类型 @throws
     */
    public void setFileContent(CreditFileModel creditFile, AnalyzeParamsModel apm) {

        try {
            // 从mongodb中获取邮件内容
            Date sentDate = creditFile.getSentDate();
            Calendar c = Calendar.getInstance();
            c.setTime(sentDate);
            int month = c.get(Calendar.MONTH) + 1;
            logger.info("文件名:{},邮件:{},月份:{}", creditFile.getFileName(), creditFile.getEmail(), month);
            EmailFile emailFile = mongoDownloadUtil.getFile(creditFile.getFileName(), creditFile.getEmail(), month);
            if (emailFile != null) {
                apm.setAttachment(emailFile.getAttachment());
                apm.setOriginContent(emailFile.getContent());
            }

            // logger.info(emailFile.getContent());
        } catch (AnalyzeBillException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }

    }

    // private void analyzeStatus(AnalyzeParamsModel apm) {
    // if (apm == null || !apm.getIsNew()) {
    // return;
    // }
    // Exception error = apm.getError();
    // if (error == null && apm.getResult() != null) {
    // // 解析成功
    // AnalyzeResult ar = apm.getResult();
    // List<CreditCard> cardList = ar.getCardList();
    // if (cardList != null && !cardList.isEmpty()) {
    // cardList.forEach(card -> {
    // JSONObject cardJson = new JSONObject();
    // // 获取需要展示的卡号
    // if (StringUtils.hasText(card.getCompleteNumbers())) {
    // cardJson.put("cardNo", card.getCompleteNumbers()
    // .substring(card.getCompleteNumbers().length() - 4,
    // card.getCompleteNumbers().length()));
    // cardJson.put("cardholder", card.getCardholder());
    // cardJson.put("bankCode", apm.getBankCode());
    // cardJson.put("bankName",
    // BankCodeEnum.getByBankCode(apm.getBankCode()).getShortName());
    // cardJson.put("shortName",
    // BankCodeEnum.getByBankCode(apm.getBankCode()).getShortName());
    // JedisClusterUtils
    // .setSave(
    // Constant.REDIS_CARDS + apm.getEmail()
    // + AnalyzeStatusListener.EMAIL_USERID_SEPARATOR + apm.getUserId(),
    // cardJson.toJSONString());
    // }
    //
    // });
    // }
    // }
    // if (apm != null) {
    // // credit_card_analyzed_status_+email+userId
    // JedisClusterUtils.incrBy(Constant.REDIS_ANALYZED_STATUS + apm.getEmail()
    // + AnalyzeStatusListener.EMAIL_USERID_SEPARATOR + apm.getUserId(), 1);
    // JedisClusterUtils.delKey(FileQueueRedisHandle.MAIL_FILE_LIST_CONTENT +
    // apm.getFileName());
    // }
    //
    // }

    private String getBankCode(String subject) {
        BankCodeEnum bank = BankCodeEnum.getByKeyword(subject);
        if (bank == null) {
            throw new RuntimeException("未查到银行,name=" + subject);
        }
        return bank.getBankCode();
    }

    /**
     * 邮件解析异常,发送邮件报警
     *
     * @param content
     *            邮件内容
     */
    private void sendMail(String content) {
        try {
            List<SendMail> sendMails = JedisClusterUtils.hashGet(Constant.redisSendMail, "SendMail", ArrayList.class);
            if (sendMails != null) {
                for (SendMail sendMail : sendMails) {

                    mailSendUtil.sendUtil(content, "邮件解析异常", sendMail.getRecipients(), sendMail.getAddresser(),
                            sendMail.getPasword(), sendMail.getHost(), sendMail.getPort());
                }
            }
        } catch (Exception e) {
            logger.error("邮件发送失败:{}", e.getMessage());
        }
    }

    private AnalyzeParamsModel setModel(AnalyzeParamsModel apm, CreditFileModel creditFile) {
        String subject = creditFile.getSubject();// 邮件主题
        String bankCode = getBankCode(subject);
        // 增加文件id
        apm.setFileId(creditFile.getId());
        apm.setEmail(creditFile.getEmail());
        apm.setBankCode(bankCode);
        // 根据bankCode设置id
        apm.setBankId(TemplateCache.bankCache.get(bankCode));
        apm.setEmailId(creditFile.getEmailId());
        apm.setSentDate(creditFile.getSentDate());
        apm.setIsNew(creditFile.getIsNew());
        apm.setFileName(creditFile.getFileName());
        // 邮件主题
        apm.setSubject(creditFile.getSubject());
        if (creditFile.getIsNew()) {
            apm.setUserId(creditFile.getUserId());
        }
        //
        setFileContent(creditFile, apm);
        return apm;
    }
}
