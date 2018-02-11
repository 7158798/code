package com.pay.card.utils;

import java.util.Collections;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.github.phantomthief.model.builder.ModelBuilder;
import com.github.phantomthief.model.builder.impl.SimpleModelBuilder;
import com.github.phantomthief.view.mapper.ViewMapper;
import com.github.phantomthief.view.mapper.impl.DefaultViewMapperImpl;
import com.github.phantomthief.view.mapper.impl.OverrideViewMapper;
import com.pay.card.model.CreditBank;
import com.pay.card.model.CreditBill;
import com.pay.card.model.CreditBillDetail;
import com.pay.card.model.CreditCard;
import com.pay.card.model.CreditRepayment;
import com.pay.card.view.AnalysisResultView;
import com.pay.card.view.CreditBankView;
import com.pay.card.view.CreditBillDetailView;
import com.pay.card.view.CreditBillView;
import com.pay.card.view.CreditCardView;
import com.pay.card.view.CreditRePayMentView;
import com.pay.card.web.context.CardBuildContext;

/**
 * @author qiaohui
 *         扫描 view，数据绑定，存入 id、value命名空间
 */
@Service
public class ApiHelper {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ApiHelper.class);
    private static final String VIEW_PATH = "com.pay.card.view";

    /**
     * 这个viewmapper存放临时的View映射定制
     * @param viewMapper
     * @return
     */
    public static final OverrideViewMapper ovMapper(ViewMapper viewMapper) {
        OverrideViewMapper overrideViewMapper = new OverrideViewMapper(viewMapper);
        overrideViewMapper.addMapper(CreditCard.class, (creditcard, buildContext2) -> new AnalysisResultView(
                        creditcard, (CardBuildContext) buildContext2));

        return overrideViewMapper;
    }

    /**
     * 这里是正常的Model到View的映射关系
     * @param pkg
     * @param ignoreViews
     * @return
     */
    public static final ViewMapper scan(String pkg, Set<Class<?>> ignoreViews) {
        DefaultViewMapperImpl viewMapper = new DefaultViewMapperImpl();
        viewMapper.addMapper(CreditBank.class, (buildContext, creditBank) -> new CreditBankView(creditBank,
                        (CardBuildContext) buildContext));
        viewMapper.addMapper(CreditCard.class, (buildContext, creditcard) -> new CreditCardView(creditcard,
                        (CardBuildContext) buildContext));
        viewMapper.addMapper(CreditBill.class, (buildContext, creditBill) -> new CreditBillView(creditBill,
                        (CardBuildContext) buildContext));
        viewMapper.addMapper(CreditBillDetail.class, (buildContext, creditBillDetail) -> new CreditBillDetailView(
                        creditBillDetail, (CardBuildContext) buildContext));
        viewMapper.addMapper(CreditRepayment.class, (buildContext, repayment) -> new CreditRePayMentView(repayment,
                        (CardBuildContext) buildContext));
        return viewMapper;
    }

    private SimpleModelBuilder<CardBuildContext> modelBuilder;

    private ViewMapper viewMapper;

    private OverrideViewMapper overrideViewMapper;

    private CardBuildContext buildContext;

    private CardBuildContext buildContext2;

    public CardBuildContext getBuildContext() {
        return buildContext;
    }

    public CardBuildContext getBuildContext2() {
        return buildContext2;
    }

    public ModelBuilder<CardBuildContext> getModelBuilder() {
        return modelBuilder;
    }

    public OverrideViewMapper getOverrideViewMapper() {
        return overrideViewMapper;
    }

    public ViewMapper getViewMapper() {
        return viewMapper;
    }

    @PostConstruct
    private void init() {
        viewMapper = scan(VIEW_PATH, Collections.emptySet());
        overrideViewMapper = ovMapper(viewMapper);
        buildContext = new CardBuildContext();
        buildContext2 = new CardBuildContext();
        modelBuilder = new SimpleModelBuilder<CardBuildContext>();
    }
}
