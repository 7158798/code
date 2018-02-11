package com.pay.aile.bill.utils;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * MailReleaseUtil.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
public class MailReleaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(MailReleaseUtil.class);

    public static void releaseFolder(Folder folder) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(true);
            } catch (MessagingException e) {
                logger.info("close folder error");
            }
        }
    }

    public static void releaseFolderAndStore(Folder folder, Store store) {
        releaseFolder(folder);
        releaseStore(store);
    }

    public static void releaseStore(Store store) {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            } catch (MessagingException e) {
                logger.info("close store error");
            }
        }
    }
}
