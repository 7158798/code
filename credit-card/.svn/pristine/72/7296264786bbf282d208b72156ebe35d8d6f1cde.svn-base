package com.pay.aile.bill.service.mail.download;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay.aile.bill.contant.BankKeywordContants;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.entity.EmailFile;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.service.CreditBankService;
import com.pay.aile.bill.service.mail.relation.CreditFileRelation;
import com.pay.aile.bill.utils.ApacheMailUtil;
import com.pay.aile.bill.utils.MailLoginUtil;
import com.pay.aile.bill.utils.MailReleaseUtil;
import com.pay.aile.bill.utils.MailSearchUtil;
import com.pay.aile.bill.utils.MongoDownloadUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

/***
 * MailOperation.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
public abstract class BaseMailOperation {

    private static final Logger logger = LoggerFactory.getLogger(BaseMailOperation.class);
    public static final String STROE_POP3 = "pop3";
    public static final String STROE_IMAP = "imap";
    public static final String FILE_NAME_SEPARATOR = "|";

    @Autowired
    private MongoDownloadUtil downloadUtil;
    @Autowired
    private CreditBankService creditBankService;

    @Autowired
    private CreditFileRelation creditFileRelation;

    /***
     * 下载邮件
     *
     * 注意126、163、和qq邮箱使用POP3授权码
     *
     * @param mailAddr
     * @param password
     * @throws Exception
     */
    public void downloadMail(CreditEmail creditEmail) throws MailBillException {
        Store store = MailLoginUtil.login(getMailProperties(), getStoreType(), creditEmail.getEmail(),
                creditEmail.getPassword());
        Folder defaultFolder = null;
        Folder[] folderArr = null;
        List<CreditFile> creditFilelist = getCreditFiles(creditEmail.getEmail());
        try {
            defaultFolder = store.getDefaultFolder();
            folderArr = defaultFolder.list();
            List<EmailFile> emailFiles = new ArrayList<>(32);
            List<CreditFile> creditFiles = new ArrayList<>(32);
            for (Folder tempFolder : folderArr) {
                Folder folder = store.getFolder(tempFolder.getName());
                folder.open(Folder.READ_ONLY);
                long startTime = System.currentTimeMillis();
                Message[] messages = MailSearchUtil.search(getKeywords(), folder);
                long endTime = System.currentTimeMillis();
                logger.debug("文件夹{}搜索到{}封邮件，耗时{}ms", tempFolder.getName(), messages.length, endTime - startTime);
                for (int i = 0; i < messages.length; i++) {
                    Message tmpMessage = messages[i];
                    String mailUID = getMailUID(folder, tmpMessage);
                    if (isDownloaded(mailUID, creditFilelist)) {
                        continue;
                    }
                    // 存入mongodb
                    EmailFile emailFile = ApacheMailUtil.getEmailFile(tmpMessage, creditEmail);
                    emailFile.setFileName(mailUID);
                    emailFiles.add(emailFile);
                    // 存入mysql
                    CreditFile creditFile = ApacheMailUtil.getCreditFile(emailFile, creditEmail);
                    creditFiles.add(creditFile);

                }
                folder.close(true);
            }
            if (emailFiles.size() > 0) {
                downloadUtil.saveFile(emailFiles, creditFiles);
            }
        } catch (MessagingException | MailBillException e) {
            logger.error("下载邮件异常");
            logger.error(e.getMessage(), e);
        } finally {
            MailReleaseUtil.releaseFolderAndStore(defaultFolder, store);
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

    /***
     * 获取已经下载的邮件
     *
     * @param email
     * @return
     */
    private List<CreditFile> getCreditFiles(String email) {
        return creditFileRelation.selectCreditFiles(email);
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
        if (folder instanceof POP3Folder) {
            return folder.getName() + FILE_NAME_SEPARATOR + ((POP3Folder) folder).getUID(message);
        }
        if (folder instanceof IMAPFolder) {
            return folder.getName() + FILE_NAME_SEPARATOR + String.valueOf(((IMAPFolder) folder).getUID(message));
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

    /***
     * 使用POP3 若使用imap需要子类复写
     *
     * @return
     */
    protected String getStoreType() {
        return BaseMailOperation.STROE_POP3;
    }

}
