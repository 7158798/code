package com.pay.aile.bill.service.mail.download;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.DateUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.pay.aile.bill.config.MemoryCache;
import com.pay.aile.bill.contant.BankKeywordContants;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetailRelation;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.entity.CreditUserBillRelation;
import com.pay.aile.bill.entity.CreditUserCardRelation;
import com.pay.aile.bill.entity.CreditUserFileRelation;
import com.pay.aile.bill.entity.EmailFile;
import com.pay.aile.bill.enums.BankCodeEnum;
import com.pay.aile.bill.enums.CommonStatus;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.job.FileQueueRedisHandle;
import com.pay.aile.bill.mapper.CreditBillDetailRelationMapper;
import com.pay.aile.bill.mapper.CreditBillMapper;
import com.pay.aile.bill.mapper.CreditCardMapper;
import com.pay.aile.bill.mapper.CreditFileMapper;
import com.pay.aile.bill.mapper.CreditUserBillRelationMapper;
import com.pay.aile.bill.mapper.CreditUserCardRelationMapper;
import com.pay.aile.bill.mapper.CreditUserFileRelationMapper;
import com.pay.aile.bill.service.CreditBankService;
import com.pay.aile.bill.service.mail.relation.CreditFileRelation;
import com.pay.aile.bill.utils.ApacheMailUtil;
import com.pay.aile.bill.utils.DateUtil;
import com.pay.aile.bill.utils.ErrorLogUtil;
import com.pay.aile.bill.utils.JedisClusterUtils;
import com.pay.aile.bill.utils.MailLoginUtil;
import com.pay.aile.bill.utils.MailProcessStatusCacheUtil;
import com.pay.aile.bill.utils.MailReleaseUtil;
import com.pay.aile.bill.utils.MongoDownloadUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

/***
 * MailOperation.java
 *
 * @author shinelon
 * @date 2017年10月30日
 */
public abstract class BaseMailOperation {
    private class BillDetailModel {
        CreditBill bill;
        List<CreditBillDetailRelation> billDetailRelationList;

        public CreditBill getBill() {
            return bill;
        }

        public List<CreditBillDetailRelation> getBillDetailRelationList() {
            return billDetailRelationList;
        }

        public void setBill(CreditBill bill) {
            this.bill = bill;
        }

        public void setBillDetailRelationList(List<CreditBillDetailRelation> billDetailRelationList) {
            this.billDetailRelationList = billDetailRelationList;
        }

    }

    private class CardBillModel {
        CreditCard card;
        List<BillDetailModel> billDetailList;

        public List<BillDetailModel> getBillDetailList() {
            return billDetailList;
        }

        public CreditCard getCard() {
            return card;
        }

        public void setBillDetailList(List<BillDetailModel> billDetailList) {
            this.billDetailList = billDetailList;
        }

        public void setCard(CreditCard card) {
            this.card = card;
        }
    }

    private static final long DEFAULT_START_SEARCH_DATE_MONTH_OFFSET = -3;

    private static final Logger logger = LoggerFactory.getLogger(BaseMailOperation.class);
    public static final String STROE_POP3 = "pop3";
    public static final String STROE_IMAP = "imap";
    public static final String FILE_NAME_SEPARATOR = "|";
    @Autowired
    private FileQueueRedisHandle fileQueueRedisHandle;
    @Autowired
    private MongoDownloadUtil downloadUtil;
    @Autowired
    private CreditBankService creditBankService;
    @Autowired
    private CreditBillDetailRelationMapper creditBillDetailRelationMapper;
    @Autowired
    private CreditFileRelation creditFileRelation;
    @Autowired
    private CreditBillMapper creditBillMapper;
    @Autowired
    private CreditCardMapper creditCardMapper;
    @Autowired
    private CreditUserFileRelationMapper userFileRelationMapper;
    @Autowired
    private CreditUserBillRelationMapper userBillRelationMapper;
    @Autowired
    private CreditUserCardRelationMapper userCardRelationMapper;
    @Autowired
    private CreditFileMapper creditFileMapper;

    @Resource
    private ErrorLogUtil errorLogUtil;

    Pattern pattern = null;

    /**
     * @Title: canLogin
     * @Description:判断此邮箱是否可以登录
     * @param creditEmail
     * @return boolean 返回类型 @throws
     */
    public boolean canLogin(CreditEmail creditEmail) throws MailBillException {
        boolean canLogin = MailLoginUtil.canLogin(getMailProperties(), getStoreType(), creditEmail.getEmail(),
                creditEmail.getPassword());
        // 初始化缓存
        if (canLogin && creditEmail.getIsNew()) {
            MailProcessStatusCacheUtil.initMailProcessStatus(creditEmail);
        }

        return canLogin;
    }

    /**
     * @Description 仅copy,不登录
     * @param creditEmail
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public void copyRelation(CreditEmail creditEmail) {
        copyUserRelation(null, creditEmail);
    }

    /**
     * @Description 登录并查询邮箱后再copy
     * @see 需要参考的类或方法
     * @author chao.wang
     * @throws MailBillException
     */
    public void copyRelationLoginSearch(CreditEmail creditEmail) throws MailBillException {
        creditEmail.setDownload(false);
        downloadMail(creditEmail);
    }

    /***
     * 下载邮件 注意126、163、和qq邮箱使用POP3授权码
     *
     * @param mailAddr
     * @param password
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    public void downloadMail(CreditEmail creditEmail) throws MailBillException {

        long s = System.currentTimeMillis();
        Store store = null;
        logger.info("creditEmailJSON==========================={}", JSONObject.toJSONString(creditEmail));
        try {
            store = MailLoginUtil.login(getMailProperties(), getStoreType(), creditEmail.getEmail(),
                    creditEmail.getPassword());
            logger.info("邮箱登录成功：邮箱名{},密码{}", creditEmail.getEmail(), creditEmail.getPassword());
        } catch (MailBillException e) {
            logger.error("邮箱登录失败：邮箱名{},密码{}", creditEmail.getEmail(), creditEmail.getPassword());
            errorLogUtil.loginErrorLog(creditEmail, e);
            throw e;
        }
        // finally {
        // // 若是新加入的邮箱,将登录状态存入缓存
        // if (creditEmail.getIsNew()) {
        // if (store == null) {
        // MailProcessStatusCacheUtil.loginFail(creditEmail);
        // } else {
        // MailProcessStatusCacheUtil.loginSuccess(creditEmail);
        // }
        // }
        // }
        LocalDate date = LocalDate.now();
        date = date.of(date.getYear(), date.getMonth(), 1);
        date = date.plusMonths(DEFAULT_START_SEARCH_DATE_MONTH_OFFSET);
        Date minDate = DateUtil.localDateToDate(date);
        Folder defaultFolder = null;
        Folder[] folderArr = null;
        // List<CreditFile> creditFilelist =
        // getCreditFiles(creditEmail.getEmail());
        // List<CreditFile> creditFilelist =
        // getCreditFiles(creditEmail.getEmail(), new
        // Long(creditEmail.getUserId()));
        List<CreditFile> creditFilelist = getCreditFiles(creditEmail.getEmail());
        // 文件名称的set
        Map<String, CreditFile> fileMap = getFileSet(creditFilelist);
        logger.info("邮:{}找到邮件个数{}", creditEmail.getEmail(), creditFilelist.size());
        String newSendDate = "";
        try {
            defaultFolder = store.getDefaultFolder();
            folderArr = defaultFolder.list();
            // List<EmailFile> emailFiles = new ArrayList<>(32);

            for (Folder tempFolder : folderArr) {
                Folder folder = store.getFolder(tempFolder.getName());
                folder.open(Folder.READ_ONLY);
                int messageCount = folder.getMessageCount();

                // 获取所有的邮件
                Message[] messages = folder.getMessages();

                // 缓存邮件数量
                if (creditEmail.getIsNew()) {
                    // 如果邮箱没有邮件
                    if (messageCount == 0) {
                        MailProcessStatusCacheUtil.setEmailNum(creditEmail, -1);
                    } else {
                        // 获取有效邮件的总数量
                        messageCount = getEmailNumber(messages, minDate, messageCount);
                        MailProcessStatusCacheUtil.setEmailNum(creditEmail, messageCount);
                    }

                }

                // Message[] messages =
                // MailSearchUtil.searchByCount(getKeywords(), folder);
                int count = 1;
                int findFileCount = 1;
                long startTime = System.currentTimeMillis();
                Date newSentDate = null;

                if (!creditEmail.getIsNew()) {
                    String newSentDateStr = JedisClusterUtils
                            .getString(Constant.REDIS_EMAIL_NEW_SENDDATE + creditEmail.getEmail());
                    if (newSentDateStr != null) {
                        newSentDate = DateUtil.parseDate(newSentDateStr);

                    }

                }

                for (int i = messages.length - 1; i >= 0; i--) {

                    Message tmpMessage = messages[i];
                    // 当后台任务爬去邮件时，需根据最新爬取时间判断是否已经爬取过了
                    if (!creditEmail.getIsNew() && newSentDate != null && newSentDate.after(tmpMessage.getSentDate())) {
                        return;
                    }
                    // 默认是爬去当前时间3个月的邮件
                    if (tmpMessage.getSentDate() != null && minDate.after(tmpMessage.getSentDate())) {
                        if (creditEmail.getIsNew()) {

                            MailProcessStatusCacheUtil.setReadEmailNum(creditEmail, messageCount);

                        }
                        return;
                    }

                    logger.info("eamil:{}=====isDownload:{}=====getIsNew:{},=====userId;{}", creditEmail.getEmail(),
                            creditEmail.isDownload(), creditEmail.getIsNew(), creditEmail.getUserId());
                    // 育检的uid
                    String mailUID = getMailUID(folder, tmpMessage);

                    // if (!creditEmail.isDownload()) {
                    // CreditFile creditFile =
                    // ApacheMailUtil.getCreditFile(emailFile, creditEmail);
                    // copyEmailFiles.add(creditFile);
                    // }
                    // 初始化正则表达式
                    if (pattern == null) {
                        initKeywordsReg();
                    }
                    // 关键字匹配
                    Matcher matcher = pattern.matcher(tmpMessage.getSubject());
                    logger.info("搜索了{}/{}", count, messageCount);
                    count++;
                    long exeTime = System.currentTimeMillis();
                    // 每一秒钟向redis中写入最新的解析邮件个数
                    if (creditEmail.getIsNew() && exeTime - startTime >= 1000) {
                        MailProcessStatusCacheUtil.setReadEmailNum(creditEmail, count);
                        startTime = exeTime;
                    }
                    // 匹配关键字
                    if (!matcher.find()) {
                        continue;
                    }
                    // 设置缓存
                    if (creditEmail.getIsNew()) {
                        JedisClusterUtils.saveString(
                                Constant.REDIS_ANALYSIS_STATUS + creditEmail.getEmail()
                                        + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(),
                                String.valueOf(findFileCount));
                        findFileCount++;
                    }

                    // if (isDownloaded(mailUID, creditFilelist)) {

                    // 文件是否已经下载过
                    if (creditEmail.isDownload() && fileMap.containsKey(mailUID)) {
                        continue;
                    } else if (!creditEmail.isDownload() && fileMap.containsKey(mailUID)) {
                        // 邮箱已被导入过，复制关系
                        List<CreditFile> creditFiles = new ArrayList<>(1);
                        CreditFile creditFile = fileMap.get(mailUID);
                        creditFile.setEmailId(creditEmail.getId());
                        creditFile.setUserId(new Long(creditEmail.getUserId()));
                        creditFile.setIsNew(true);
                        creditFiles.add(creditFile);
                        copyFile(creditFiles, creditEmail);

                    } else {
                        logger.info("邮箱:{},找到文件:{}", creditEmail.getEmail(), tmpMessage.getSubject());
                        EmailFile emailFile = ApacheMailUtil.getEmailFile(tmpMessage, creditEmail);
                        emailFile.setFileName(mailUID);
                        List<EmailFile> emailFiles = new ArrayList<>(1);
                        List<CreditFile> creditFiles = new ArrayList<>(1);
                        // 存入mongodb
                        emailFiles.add(emailFile);
                        // 存入mysql
                        CreditFile creditFile = ApacheMailUtil.getCreditFile(emailFile, creditEmail);
                        creditFiles.add(creditFile);

                        if (creditEmail.isDownload()) {
                            saveFile(emailFiles, creditFiles, creditEmail);
                        }
                        long endTime = System.currentTimeMillis();

                        logger.info("搜寻邮件,邮箱:{},耗时{}ms", creditEmail.getEmail(), endTime - startTime);
                    }

                }
                if (!creditEmail.getIsNew()) {
                    // 设置已解析的邮箱的时间
                    JedisClusterUtils.saveString(Constant.REDIS_EMAIL_NEW_SENDDATE + creditEmail.getEmail(),
                            DateUtil.formatDate(messages[messages.length - 1].getSentDate()));
                }

                if (folder.isOpen()) {
                    folder.close(true);

                }
            }
            logger.info("creditEmail.isDownload()======================================{}", creditEmail.isDownload());

            // if (creditEmail.isDownload()) {
            // saveFile(emailFiles, creditFiles, creditEmail);
            // } else {
            // copyUserRelation(copyEmailFiles, creditEmail);
            // }
        } catch (MessagingException | MailBillException e) {
            logger.error("下载邮件异常");
            logger.error(e.getMessage(), e);
            errorLogUtil.downloadErrorLog(creditEmail, e);
        } finally {
            MailReleaseUtil.releaseFolderAndStore(defaultFolder, store);

            long e = System.currentTimeMillis();

            logger.info("一共執行了==={}", e - s);
        }
    }

    public void downloadMail(String mailAddr, String password) throws MailBillException {
        CreditEmail creditEmail = new CreditEmail(mailAddr, password);
        this.downloadMail(creditEmail);
    }

    /***
     * 邮箱登录配置
     *
     * @return
     */
    public abstract Properties getMailProperties();

    private void copyFile(List<CreditFile> creditFileList, CreditEmail creditEmail) {

        creditFileRelation.copyNotExitsCreditFile(creditFileList, creditEmail);
        fileQueueRedisHandle.bathLeftPushFile(creditFileList);
        // if (creditEmail.getIsNew()) {
        // fileQueueRedisHandle.bathLeftPushFile(creditFileList);
        // } else {
        // fileQueueRedisHandle.bathPushFile(creditFileList);
        // }
    }

    /**
     * @Title: copyUserRelation
     * @Description: 复制文件、账单、信用卡的关系
     * @param creditFiles
     * @param creditEmail
     * @return void 返回类型 @throws
     */

    @Transactional
    private void copyUserRelation(List<CreditFile> creditFiles, CreditEmail creditEmail) {
        try {
            Long userId = new Long(creditEmail.getUserId());
            // 查询此邮箱已经下载的邮件
            List<CreditFile> fileList = creditFileMapper.findByRelation(creditEmail.getEmail());
            List<CreditFile> saveFileList = Collections.emptyList();
            logger.info("fileList==================================={}", fileList.size());
            if (creditFiles != null) {
                // 对邮件进行过滤
                Set<String> fileNameSet = new HashSet<String>();
                creditFiles.forEach(newFile -> {
                    // 时间+主题
                    String fileStr = DateUtils.format(newFile.getSentDate(), "yyyyMMddHHmmss", Locale.CHINA)
                            + newFile.getSubject();
                    fileNameSet.add(fileStr);
                });

                // 获取需要保存的文件
                saveFileList = fileList.stream().filter(file -> fileNameSet.contains(
                        DateUtils.format(file.getSentDate(), "yyyyMMddHHmmss", Locale.CHINA) + file.getSubject()))
                        .collect(Collectors.toList());
            } else {
                saveFileList = fileList;
            }
            // 保存
            if (saveFileList != null && saveFileList.size() > 0) {
                // 用户和文件的关系
                List<CreditUserFileRelation> fileRelationList = new ArrayList<CreditUserFileRelation>();
                // 要保存的文件id
                List<Long> fileIdList = new ArrayList<Long>();
                saveFileList.forEach(file -> {
                    CreditUserFileRelation relation = new CreditUserFileRelation();
                    relation.setFileId(file.getId());
                    relation.setUserId(userId);
                    fileIdList.add(file.getId());
                    // 设置需要查询的文件id

                    fileRelationList.add(relation);

                });
                // 保存user-file-relation
                userFileRelationMapper.batchInsert(fileRelationList);
                // 需要查询关系的 用户
                if (!fileIdList.isEmpty()) {
                    // 根据file_id找出所有的bill,根据bill找出所有的card,然后再保存
                    List<CreditBill> billList = creditBillMapper
                            .selectList(new EntityWrapper<CreditBill>().in("file_id", fileIdList));
                    List<CreditCard> cardList = new ArrayList<>();
                    List<CreditCard> saveCardList = new ArrayList<>();
                    List<CreditBill> saveBillList = new ArrayList<>();
                    List<CardBillModel> saveCardBillModelList = new ArrayList<CardBillModel>();
                    List<Long> cardIds = new ArrayList<>();
                    if (billList != null && !billList.isEmpty()) {
                        // 根据bill找出所有的card
                        cardIds = billList.stream().map(bill -> {
                            return bill.getCardId();
                        }).collect(Collectors.toList());
                        cardList = creditCardMapper.selectBatchIds(cardIds);
                    }
                    for (CreditCard card : cardList) {
                        CreditCard newCard = new CreditCard();
                        BeanUtils.copyProperties(card, newCard);
                        newCard.setId(null);
                        newCard.setUserId(new Long(creditEmail.getUserId()));
                        newCard.setStatus(1);
                        CardBillModel cbm = new CardBillModel();
                        cbm.card = newCard;
                        cbm.billDetailList = new ArrayList<>();
                        List<CreditBill> blist = billList.stream()
                                .filter(bill -> bill.getCardId().longValue() == card.getId().longValue())
                                .collect(Collectors.toList());
                        logger.debug("******cardId={},blistSize={}", card.getId(), blist.size());
                        blist.forEach(bill -> {
                            CreditBill b = new CreditBill();
                            BeanUtils.copyProperties(bill, b);
                            b.setId(null);
                            b.setCardId(null);
                            b.setNewStatus(1);
                            BillDetailModel cdm = new BillDetailModel();
                            cdm.bill = b;
                            cdm.billDetailRelationList = new ArrayList<>();
                            // 通过billId查询bill-detail-relation
                            String year = b.getYear();
                            String month = b.getMonth();
                            List<CreditBillDetailRelation> cbdrList = creditBillDetailRelationMapper
                                    .findByBillId(new Integer(year), new Integer(month), Arrays.asList(bill.getId()));
                            logger.debug("******billId={},cbdrListSize={}", bill.getId(), cbdrList.size());
                            if (cbdrList != null && !cbdrList.isEmpty()) {
                                cbdrList.forEach(cbdr -> {
                                    CreditBillDetailRelation r = new CreditBillDetailRelation();
                                    r.setBillId(null);
                                    r.setBillDetailId(cbdr.getBillDetailId());
                                    r.setStatus(CommonStatus.AVAILABLE.value);
                                    cdm.billDetailRelationList.add(r);
                                });
                            }
                            cbm.billDetailList.add(cdm);
                            saveBillList.add(b);
                        });
                        saveCardList.add(newCard);
                        saveCardBillModelList.add(cbm);
                    }
                    logger.debug("****saveCardBillModelList before ==={}", JSON.toJSONString(saveCardBillModelList));
                    // 保存card,保存bill,保存user_card_relation,保存user_bill_relation,保存bill_detail_relation
                    // 保存card
                    if (!saveCardList.isEmpty()) {
                        creditCardMapper.batchInsert(saveCardList);
                    }
                    // 将bill中设置好cardId
                    if (!saveCardBillModelList.isEmpty()) {
                        saveCardBillModelList.forEach(cbm -> {
                            CreditCard card = cbm.card;
                            List<BillDetailModel> bdmList = cbm.billDetailList;
                            bdmList.forEach(bdm -> {
                                bdm.bill.setCardId(card.getId());
                            });
                        });
                    }
                    // 保存bill
                    if (!saveBillList.isEmpty()) {
                        creditBillMapper.batchInsert(saveBillList);
                    }
                    // 保存user-card-relation
                    if (!saveCardList.isEmpty()) {
                        List<CreditUserCardRelation> userCardRelationList = new ArrayList<>();
                        saveCardList.forEach(card -> {
                            CreditUserCardRelation cucr = new CreditUserCardRelation();
                            cucr.setUserId(new Long(creditEmail.getUserId()));
                            cucr.setCardId(card.getId());
                            cucr.setCreateDate(new Date());
                            if (card.getDueDate() == null) {
                                cucr.setStatus(0);
                            }
                            userCardRelationList.add(cucr);
                        });
                        userCardRelationMapper.batchInsert(userCardRelationList);
                    }
                    // 保存user-bill-relation
                    if (!saveBillList.isEmpty()) {
                        List<CreditUserBillRelation> userBillRelationList = new ArrayList<>();
                        saveBillList.forEach(bill -> {
                            CreditUserBillRelation cubr = new CreditUserBillRelation();
                            cubr.setUserId(new Long(creditEmail.getUserId()));
                            cubr.setBillId(bill.getId());
                            cubr.setCreateDate(new Date());
                            userBillRelationList.add(cubr);
                        });
                        userBillRelationMapper.batchInsert(userBillRelationList);
                    }
                    // 保存bill-detail-relation
                    Map<String, List<CreditBillDetailRelation>> saveBillDetailRelationMap = new HashMap<>();
                    if (!saveCardBillModelList.isEmpty()) {
                        // 根据year-month,将bill-detail-relation汇总到map中
                        saveCardBillModelList.forEach(cbm -> {
                            if (cbm.billDetailList != null && !cbm.billDetailList.isEmpty()) {
                                cbm.billDetailList.forEach(item -> {
                                    CreditBill bill = item.bill;
                                    if (item.billDetailRelationList != null && !item.billDetailRelationList.isEmpty()) {
                                        item.billDetailRelationList.forEach(bdrl -> bdrl.setBillId(bill.getId()));
                                    }
                                    String key = bill.getYear() + "-" + bill.getMonth();
                                    List<CreditBillDetailRelation> l = saveBillDetailRelationMap.get(key);
                                    if (l == null) {
                                        l = new ArrayList<>();
                                    }
                                    l.addAll(item.billDetailRelationList);
                                    saveBillDetailRelationMap.put(key, l);
                                });
                            }
                        });
                    }
                    if (!saveBillDetailRelationMap.isEmpty()) {
                        saveBillDetailRelationMap.entrySet().stream().forEach(map -> {
                            String yearMonth = map.getKey();
                            String[] sarray = yearMonth.split("-");
                            String year = sarray[0];
                            String month = sarray[1];
                            List<CreditBillDetailRelation> relationList = map.getValue();
                            creditBillDetailRelationMapper.batchInsert(new Integer(year), new Integer(month),
                                    relationList);
                        });
                    }

                    logger.debug("****saveCardBillModelList after ==={}", JSON.toJSONString(saveCardBillModelList));
                    saveCardList.forEach(card -> {
                        JSONObject cardJson = new JSONObject();
                        if (StringUtils.isNotEmpty(card.getCompleteNumbers())) {
                            logger.info("carlist--------------------------{}", JSONObject.toJSONString(card));
                            cardJson.put("cardNo", card.getCompleteNumbers().subSequence(
                                    card.getCompleteNumbers().length() - 4, card.getCompleteNumbers().length()));
                            cardJson.put("cardholder", card.getCardholder());
                            cardJson.put("bankCode", MemoryCache.bankCache.get(card.getBankId()));
                            cardJson.put("bankName", BankCodeEnum
                                    .getByBankCode(MemoryCache.bankCache.get(card.getBankId())).getShortName());
                            MailProcessStatusCacheUtil.setAnalyzedCards(creditEmail, cardJson.toJSONString());
                        }
                    });
                    int billNum = saveBillList.size();
                    MailProcessStatusCacheUtil.setDownloadNum(creditEmail, billNum);
                    MailProcessStatusCacheUtil.setAnalyzedBillNum(creditEmail, billNum);
                }

            } else {

                MailProcessStatusCacheUtil.setDownloadNothing(creditEmail);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            MailProcessStatusCacheUtil.setDownloadNothing(creditEmail);
        }
    }

    /***
     * 获取已经下载的邮件
     *
     * @param email
     * @return
     */
    private List<CreditFile> getCreditFiles(String email) {
        return creditFileRelation.selectDownloadedCreditFiles(email);
    }

    private List<CreditFile> getCreditFiles(String email, Long userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("email", email);
        map.put("userId", userId);
        return creditFileRelation.selectDownloadedCreditFiles(email);
    }

    /**
     *
     * @Title: getEmailNumber
     * @Description:获取邮件的总数量
     * @param messages
     * @param date
     * @param count
     * @return int 返回类型 @throws
     */
    private int getEmailNumber(Message[] messages, Date date, int count) {
        int myCount = count / 2;
        int js = count / 2;
        Message temp = messages[myCount];
        try {
            while (date.after(temp.getSentDate())) {
                myCount = myCount / 2;
                js = js + myCount;

                temp = messages[js];

            }
            return myCount * 2;
            // if(date.before(temp.getSentDate())) {
            // return count;
            // }else {
            // getEmailNumber(messages, date,myCount,js+myCount);
            // }
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            return 0;
        }

    }

    private Map<String, CreditFile> getFileSet(List<CreditFile> list) {
        Map<String, CreditFile> fileMap = new HashMap<String, CreditFile>();
        list.forEach(tfile -> {
            // fileSet.add(tfile.getFileName());
            fileMap.put(tfile.getFileName(), tfile);
        });
        return fileMap;
    }

    /***
     * 获取当前邮箱中邮件的唯一ID
     *
     * @param folder
     * @param message
     * @return
     * @throws MessagingException
     */
    private String getMailUID(Folder folder, Message message) throws MessagingException {
        if (!folder.isOpen()) {
            folder.open(Folder.READ_ONLY);
        }
        if (folder instanceof POP3Folder) {
            String uid = ((POP3Folder) folder).getUID(message);
            if (!StringUtils.isEmpty(uid)) {
                return folder.getName() + FILE_NAME_SEPARATOR + uid;
            }

        }
        if (folder instanceof IMAPFolder) {
            String uid = String.valueOf(((IMAPFolder) folder).getUID(message));
            if (!StringUtils.isEmpty(uid)) {
                return folder.getName() + FILE_NAME_SEPARATOR + uid;
            }
        }
        logger.warn("获取邮件uid异常");
        return UUID.randomUUID().toString();
    }

    /***
     * 判断邮件是否已经下载
     *
     * @param mailUID
     * @param list
     * @return
     */
    private boolean isDownloaded(String mailUID, List<CreditFile> list) {
        List<String> isDownloadedList = list.stream().map(e -> e.getFileName()).filter(e -> mailUID.equals(e))
                .collect(Collectors.toList());
        return isDownloadedList.size() > 0;
    }

    /**
     * @Description 若是新加入的邮箱,则存入当前需解析的账单数量
     * @param emailFiles
     * @param creditFiles
     * @param creditEmail
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    private void saveFile(List<EmailFile> emailFiles, List<CreditFile> creditFiles, CreditEmail creditEmail) {
        // if (creditEmail.getIsNew()) {
        // if (emailFiles.isEmpty()) {
        // MailProcessStatusCacheUtil.setDownloadNothing(creditEmail);
        // } else {
        // MailProcessStatusCacheUtil.setDownloadNum(creditEmail,
        // emailFiles.size());
        // }
        // }
        logger.info("调用saveFile3======================================={},{}", emailFiles.size(), creditFiles.size());
        if (emailFiles.size() > 0) {
            logger.info("调用downloadUtil.saveFile=======================================");
            downloadUtil.saveFile(emailFiles, creditFiles, creditEmail);
            // fileQueueRedisHandle.bathLeftPushFile(creditFiles);
            // if (creditEmail.getIsNew()) {
            // fileQueueRedisHandle.bathLeftPushFile(creditFiles);
            // } else {
            // fileQueueRedisHandle.bathPushFile(creditFiles);
            // }
        }
    }

    /***
     * 使用关键字搜索邮件，可以定制Override
     *
     * @return
     */
    protected String getKeywords() {
        if (StringUtils.isEmpty(BankKeywordContants.ALL_BANK_KEYWORDS)) {
            creditBankService.initKeywords();
        }
        return BankKeywordContants.ALL_BANK_KEYWORDS;
    }

    protected String getKeywordsReg() {
        if (StringUtils.isEmpty(BankKeywordContants.ALL_BANK_KEYWORDS_REG)) {
            creditBankService.initKeywordsReg();
        }
        return BankKeywordContants.ALL_BANK_KEYWORDS_REG;
    }

    /***
     * 使用POP3 若使用imap需要子类复写
     *
     * @return
     */
    protected String getStoreType() {
        return BaseMailOperation.STROE_POP3;
    }

    protected void initKeywordsReg() {
        String regEx = getKeywordsReg();
        pattern = Pattern.compile(regEx);
    }
}
