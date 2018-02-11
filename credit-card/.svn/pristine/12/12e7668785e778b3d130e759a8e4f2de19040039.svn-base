package com.pay.aile.bill.analyze;

/**
 *
 * @author Charlie
 * @description 邮件内容提取 从文件中提取出账单的格式化后的内容
 */
public interface MailContentExtractor {
    /**
     *
     * @param is
     * @return 提取邮件内容 提取失败返回null
     */
    public String extract(String content, String... tagName);

    /**
     *
     * @param suffix
     *            文件后缀
     * @return 根据文件后缀判断是否支持
     */
    public boolean support(String suffix);
}
