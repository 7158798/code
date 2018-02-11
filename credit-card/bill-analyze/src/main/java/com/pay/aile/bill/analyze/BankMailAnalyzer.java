package com.pay.aile.bill.analyze;

import com.pay.aile.bill.exception.AnalyzeBillException;
import com.pay.aile.bill.model.AnalyzeParamsModel;

/**
 *
 * @author Charlie
 * @description 银行账单解析器 解析经格式化后的邮件内容
 */
public interface BankMailAnalyzer {

    /**
     *
     * @param content
     *            解析邮件内容
     */
    public void analyze(AnalyzeParamsModel apm) throws AnalyzeBillException;

    /**
     *
     * @param name
     *            文件名称
     * @return 根据一定规则(比如按照文件名是否包含该银行名称)判断是否支持解析
     */
    public boolean support(String name);

}
