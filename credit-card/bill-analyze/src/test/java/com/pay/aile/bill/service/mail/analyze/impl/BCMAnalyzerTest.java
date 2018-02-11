package com.pay.aile.bill.service.mail.analyze.impl;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillAnalyzeApplication;
import com.pay.aile.bill.analyze.BankMailAnalyzer;
import com.pay.aile.bill.exception.AnalyzeBillException;
import com.pay.aile.bill.mapper.CreditTemplateMapper;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.MongoDownloadUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillAnalyzeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BCMAnalyzerTest {

    @Resource(name = "BCMAnalyzer")
    private BankMailAnalyzer BCMAnalyzer;
    @Autowired
    private MongoDownloadUtil downloadUtil;

    @Autowired
    CreditTemplateMapper creditTemplateMapper;

    @Test
    public void test() throws AnalyzeBillException {
        String content = "";
        // try {
        // // content = downloadUtil.getFile("交通银行信用卡电子账单");
        // // content =
        // // downloadUtil.getFile("0bd7ced8-0466-4469-8d5b-17503a5e2d40");
        // } catch (AnalyzeBillException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        content = "-------- 转发邮件信息 -------- 发件人：\"交通银行太平洋信用卡中心\" &lt;PCCC@bocomcc.com&gt; 发送日期：2017-11-29 17:12:49 收件人：\"jinjing_0316@126.com\" &lt;jinjing_0316@126.com&gt; 主题：交通银行信用卡电子账单 尊敬的 金晶先生您好！ 感谢您使用交通银行信用卡，以下是您2017年11月份的信用卡电子账单。 交通银行维萨标准卡号:458123******2811 账单周期：2017/10/25-2017/11/24 本期账务说明 到期还款日 2017/12/18 本期应还款额 1666.00 $0.00 最低还款额 166.60 $0.00 信用额度 17000 $2660 取现额度 8500 $1330 本期账单应还款额= 上期账单应还款额- 还款/退货/费用返还+ 消费/取现/其他费用 人民币账户 1666.00 175.65 175.65 1666.00 美元账户 0.00 0.00 0.00 0.00 本期账务明细 以下是您的还款、退货及费用返还明细 交易日期 记账日期 交易说明 交易币种/金额 入账币种/金额 人民币账户明细 主卡卡号末四位2811 2017/11/15 2017/11/15 代发款项信用卡还款 RMB175.65 RMB175.65 以下是您的消费、取现及其他费用明细 交易日期 记账日期 交易说明 交易币种/金额 入账币种/金额 人民币账户明细 主卡卡号末四位2811 2017/11/12 2017/11/13 POS消费－银联北京云杰祥航空服务有限公司 RMB1666.00 RMB1666.00 感谢您对交通银行信用卡业务的支持，我行将竭诚为您提供服务。 ★本账单周期透支利率为日息0.050%，年化利率18.250%；下账单周期透支利率为日息0.050%，年化利率18.250%。透支利息按日计算，受大小月天数不同及客户还款情况不同等因素的影响，实际年化利率与公示利率可能存在差异。 ★上述交易说明中的商户名称仅供您参考，如与签购单不符，请以签购单为准。 ★若您的人民币或美元账户的“本期账单应还款额”显示为负数，表示本期该人民币或美元账户中尚有存款，无需您另行还款，本期账单仅供您作为对账参考。 ★若您已签约自动还款协议维护业务，建议您保持约定还款日当天全天余额充足，以便于成功扣款，成功扣款后两天内将自动恢复额度，您可通过我行微信、买单吧APP及官网关注您的账务情况。 本期积分说明 积分余额 2017/11/30到期积分 2017/12/31到期积分 2018/01/31到期积分 人民币账户 3546 --- --- --- 美元账户 0 --- --- --- 客服热线：①.普/金卡用户请拔打：400-800-9888②.白金卡客户请拔打：400-866-6888③.联名卡客户请拔打：400-889-3888欢迎登录交通银行信用卡网站http://creditcard.bankcomm.com/";
        // TextExtractUtil.parseHtml(content, "td");
        AnalyzeParamsModel amp = new AnalyzeParamsModel();
        amp.setOriginContent(content);
        amp.setBankCode("BCM");
        amp.setEmail("123@qq.com");
        amp.setBankId(1L);
        amp.setEmailId(1L);
        BCMAnalyzer.analyze(amp);
    }

}
