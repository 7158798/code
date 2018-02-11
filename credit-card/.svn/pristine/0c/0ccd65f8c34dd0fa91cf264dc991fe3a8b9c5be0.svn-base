package com.pay.aile.bill.utils;

import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFReader {

    public static String readContent(InputStream fis) {
        // 加载 pdf 文档,获取PDDocument文档对象
        String content = null;
        try {
            // 加载 pdf 文档,获取PDDocument文档对象
            PDDocument document = PDDocument.load(fis);
            int pages = document.getNumberOfPages();
            // 读文本内容
            PDFTextStripper stripper = new PDFTextStripper();
            // 设置按顺序输出
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(pages);
            content = stripper.getText(document);
            // 关闭输入流
            document.close();
            fis.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }
}
