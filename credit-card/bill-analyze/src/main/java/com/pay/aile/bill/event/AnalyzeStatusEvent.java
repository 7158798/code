package com.pay.aile.bill.event;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.context.ApplicationEvent;

import com.pay.aile.bill.model.AnalyzeParamsModel;

public class AnalyzeStatusEvent extends ApplicationEvent {

    /**
     * @author chao.wang
     */
    private static final long serialVersionUID = 1762186639770623850L;

    private AnalyzeParamsModel apm;

    public AnalyzeStatusEvent(AnalyzeParamsModel source) {
        super(source);
        AnalyzeParamsModel newAmp = new AnalyzeParamsModel();
        BeanCopier copier = BeanCopier.create(AnalyzeParamsModel.class, AnalyzeParamsModel.class, false);
        copier.copy(source, newAmp, null);

        apm = newAmp;
    }

    public AnalyzeParamsModel getApm() {
        return apm;
    }

}
