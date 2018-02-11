package com.pay.aile.bill.contant;

public class Constant {

    /**
     * Hash
     *
     * hkey:credit_card_login_status
     *
     * key:email_userId value:loginStatus
     *
     * 存放用户邮箱登录状态
     */
    public static String REDIS_LOGGIN_STATUS = "credit_card_login_status";

    /**
     * Set
     *
     * key:credit_card_cards_${email}_${userId}
     *
     * value:{{"cardNo":"1234","bankCode":"ABC"},
     * {"cardNo":"5678","bankCode":"CEB"}}
     *
     * 存放已解析的信用卡
     */
    public static String REDIS_CARDS = "credit_card_cards_";

    /**
     * key:credit_card_analysis_status_${email}_${userId}
     *
     * value:5(存放已下载的账单数量)
     */
    public static String REDIS_ANALYSIS_STATUS = "credit_card_analysis_status_";

    /**
     * key:credit_card_analyzed_status_${email}_${userId}
     *
     * value:1
     *
     * 存放已经解析完的账单数量
     */
    public static String REDIS_ANALYZED_STATUS = "credit_card_analyzed_status_";

    /**
     * 郵件的數量
     */
    public static String REDIS_EMAIL_NUMBER_STATUS = "credit_email_number_status_";
    /**
     * 已讀郵件數量
     */
    public static String REDIS_EMAIL_READ_NUMBER_STATUS = "credit_email_number_read_status_";
    /**
     * 邮箱登录失败
     */
    public static String REDIS_EMAIL_LOGIN_FIAL = "credit_card_email_login_fail_";

    public static String REDIS_EXISTS_CARD_NO = "credit_card_exists_%s_%s";

    /**
     * email 和user之间分隔符
     */
    public static final String EMAIL_USERID_SEPARATOR = "_";

    /**
     * 郵件的數量
     */
    public static String REDIS_EMAIL_NEW_SENDDATE = "credit_email_new_senddate";

    /**
     * 存储银行id+cardnumber
     */
    public static String REDIS_EXISTS_BANK_CARD = "credit_card_bank_card_exist_%s_%s";
    
    public static String REDIS_EMAIL_COOKIE = "credit_eamil_cookie_%s_%s";
}
