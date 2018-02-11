package com.pay.aile.bill.analyze.extract;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.MailContentExtractor;
import com.pay.aile.bill.utils.TextExtractUtil;

/**
 *
 * @author Charlie
 * @description
 */
@Service
public class PdfExtractor implements MailContentExtractor {

    @Override
    public String extract(String content, String... tagName) {
        return TextExtractUtil.parsePdf(content);
    }

    @Override
    public boolean support(String suffix) {
        return StringUtils.hasText(suffix) && "pdf".equalsIgnoreCase(suffix);
    }

}
