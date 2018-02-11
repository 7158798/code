
/**
* @Title: HTML5Controller.java
* @Package com.pay.card.api
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月22日
* @version V1.0
*/

package com.pay.card.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName: HTML5Controller
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月22日
 *
 */
@Controller
@RequestMapping("/html")
public class HTML5Controller {
    /*
     * 帮助
     */
    @RequestMapping(value = "/help")
    public String heip() {
        return "static/question";
    }

    @RequestMapping(value = "/importQQFail")
    public String importQQFail(String email) {
        return "static/qq_fail";

    }

    /*
     * 导入失败
     */
    @RequestMapping(value = "/importWYFail")
    public String importWYFail(String email) {
        return "static/wangyi_fail";

    }

    @RequestMapping(value = "/mailImport")
    public String mailImport(String email) {
        return "static/mailImport";
    }

    @RequestMapping(value = "/notSupport")
    public String notSupport(String email) {
        return "static/CMD";
    }

    /*
     * 协议
     */
    @RequestMapping(value = "/protocol")
    public String protocol() {
        return "static/agreement";
    }
}
