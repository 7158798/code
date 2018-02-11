package com.pay.card;

public class Constants {

    public static final int COUNT_PER_PAGE = 10;

    public static final int ACTION_COLLECT = 0;

    public static final int ACTION_UNCOLLECT = 1;

    public static final int CODE_SUCCESS = 0;

    public static final int CODE_FAILED = 1;

    public static final int INVALID = 0;

    public static final int VALID = 1;

    public static final int PAGE_SIZE = 10;

    /**
     * Hash hkey:credit_card_login_status key:email_userId value:loginStatus
     * 存放用户邮箱登录状态
     */
    public static String REDIS_LOGGIN_STATUS = "credit_card_login_status";

    /**
     * Set key:credit_card_cards_${email}
     * value:{{"cardNo":"1234","bankCode":"ABC"},
     * {"cardNo":"5678","bankCode":"CEB"}} 存放已解析的信用卡
     */
    public static String REDIS_CARDS = "credit_card_cards_";

    /**
     * key:credit_card_analysis_status_${email} value:5(存放已下载的账单数量)
     */
    public static String REDIS_ANALYSIS_STATUS = "credit_card_analysis_status_";

    /**
     * key:credit_card_analyzed_status_${email} value:1 存放已经解析完的账单数量
     */
    public static String REDIS_ANALYZED_STATUS = "credit_card_analyzed_status_";
    /**
     * email的队列
     */
    public static final String MAIL_DOWANLOD_JOB_CONTENT = "aile-mail-job-content-";
    /**
     * 未解析的招商银行简版账单
     */
    public static final String CREDIT_CMB_SIMPLE_NOT_SUPPORT = "credit-cmb-simple-not-support-%s-%s";

    public static String REDIS_REFRESH_STATUS = "credit_refresh_status_%s";

    public static String REDIS_EXISTS_CARD_NO = "credit_card_exists_%s_%s";

    public static String REDIS_USERID_KEY = "redis_userId_key_";

    /**
     * 郵件的數量
     */
    public static String REDIS_EMAIL_NUMBER_STATUS = "credit_email_number_status_";
    /**
     * 已讀郵件數量
     */

    public static String REDIS_EMAIL_READ_NUMBER_STATUS = "credit_email_number_read_status_";

}
