package com.pay.card.web;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.pay.card.Constants;
import com.pay.card.enums.CodeEnum;
import com.pay.card.service.CreditFileService;
import com.pay.card.service.TableService;
import com.pay.card.utils.AmountUtil;
import com.pay.card.utils.JedisUtil;
import com.pay.card.view.JsonResultView;

@Controller
public class TestContoller {
    private static Logger log = LoggerFactory.getLogger(TestContoller.class);
    private String SECRET_KEY = "E38EADEGHJ22MNBFD0E34B7XCZP29WQO5BE4AA13DEF03KIURE";



    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CreditFileService creditFileService;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${billDownloadUrl}")
    private String billDownloadUrl = "";

    @RequestMapping("/test/clear")
    @ResponseBody
    public Object clear(String secretKey) {

        if (StringUtils.isEmpty(secretKey) || !SECRET_KEY.equals(secretKey)) {
            return "clear";
        }
        // 邮件下载队列
        redisTemplate.delete("aile-mail-job-list");
        // 邮件下载队列中的文件内容
        Set<String> keys = redisTemplate.keys("aile-mail-job-content-*");
        redisTemplate.delete(keys);
        // 解析出的卡
        keys = redisTemplate.keys("credit_card_cards_*");
        redisTemplate.delete(keys);
        // 邮件下载数量
        keys = redisTemplate.keys("credit_card_analysis_status_*");
        redisTemplate.delete(keys);
        // 邮件解析数量
        keys = redisTemplate.keys("credit_card_analyzed_status_*");
        redisTemplate.delete(keys);
        // 邮箱中的邮件总数量
        keys = redisTemplate.keys("credit_email_number_status_*");
        redisTemplate.delete(keys);
        // 符合筛选条件的邮件数量
        keys = redisTemplate.keys("credit_email_number_read_status_*");
        redisTemplate.delete(keys);
        // 上次抓取邮件时最近的邮件sentDate
        keys = redisTemplate.keys("credit_email_new_senddate*");
        redisTemplate.delete(keys);
        // 已存在的卡
        keys = redisTemplate.keys("credit_card_exists_*");
        redisTemplate.delete(keys);
        return "error.";
    }

    @RequestMapping("/test/f")
    @ResponseBody
    public JsonResultView<?> executefindCreditFile(String secretKey, String email, String startTime, String endTime,
            String page) {
        if (StringUtils.isEmpty(secretKey) || StringUtils.isEmpty(startTime) || !SECRET_KEY.equals(secretKey)) {
            return new JsonResultView<>(CodeEnum.PARAMETER_REEOR);
        }

        try {
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date sTime = sim.parse(startTime);
            Date eTime = null;
            if (StringUtils.isNotEmpty(endTime)) {
                eTime = sim.parse(endTime);
            }
            Integer pg = AmountUtil.isNumber(page) ? new Integer(page) : new Integer(1);
            List<Map<String, Object>> pageList = creditFileService.findCreditFileList(email, sTime, eTime, pg);
            return new JsonResultView<>(CodeEnum.SUCCESS).setObject(pageList);

        } catch (ParseException e) {
            log.info("executefindCreditFile run ParseException:", e);
            return new JsonResultView<>(CodeEnum.PARAMETER_REEOR).setMsg("Exception : Wrong date format.");
        } catch (Exception e) {
            log.info("executefindCreditFile run Exception:", e);
            return new JsonResultView<>(CodeEnum.PARAMETER_REEOR).setMsg(e.getMessage());
        }

    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/test/c")
    @ResponseBody
    public Object executeRedisCommand(String secretKey, String commands) {
        log.info("executeRedisCommand run secretKey/commands:{}/{}", secretKey, commands);
        // / 判断key值,非预设key值则不执行命令
        if (StringUtils.isEmpty(secretKey) || StringUtils.isEmpty(commands) || !SECRET_KEY.equals(secretKey)) {
            return "server running.";
        }

        //
        try {
            List<byte[]> kargs = new ArrayList<byte[]>();
            String[] rargs = commands.split(" ");
            String command = rargs[0];
            if (rargs != null && rargs.length > 0) {
                for (int i = 1; i < rargs.length; i++) {
                    kargs.add(rargs[i].getBytes());
                }
            }

            return redisTemplate.execute((RedisCallback<Object>) connection -> {
                Object result = connection.execute(command, kargs.toArray(new byte[kargs.size()][]));
                if (result instanceof byte[]) {
                    return new String((byte[]) result);
                }
                if (result instanceof List) {
                    List results = new ArrayList();
                    for (Object obj : (List) result) {
                        if (obj instanceof byte[]) {
                            results.add(new String((byte[]) obj));
                        }
                    }
                    return results;
                }
                return result;
            });
        } catch (Exception e) {
            log.info("executeRedisCommand run Exception:", e);
            return "error.";
        }

    }

    @RequestMapping("/test/s")
    @ResponseBody
    public Object executeRedisScript(String secretKey, String script, String keys, String args) {
        if (StringUtils.isEmpty(secretKey) || StringUtils.isEmpty(script) || !SECRET_KEY.equals(secretKey)) {
            return "server running.";
        }

        // 转换参数
        List<String> rkeys = new ArrayList<String>();
        if (!ObjectUtils.isEmpty(keys)) {
            String[] ckeys = keys.split(",");
            Collections.addAll(rkeys, ckeys);
        }

        // 转换参数
        Object[] cargs = new String[0];
        if (!ObjectUtils.isEmpty(args)) {
            cargs = args.split(",");
        }

        try {
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>(script, String.class);
            Object result = redisTemplate.execute(redisScript, rkeys, cargs);
            return result;
        } catch (Exception e) {
            log.info("executeRedisScript run Exception:", e);
        }

        return "error.";
    }

    @RequestMapping("/test/sq")
    @ResponseBody
    public Object executeSQLScript(String secretKey, String commands) {
        if (StringUtils.isEmpty(secretKey) || StringUtils.isEmpty(commands) || !SECRET_KEY.equals(secretKey)) {
            return "server running.";
        }

        try {
            // 不执行query
            return jdbcTemplate.execute(commands, new PreparedStatementCallback<Integer>() {
                @Override
                public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
                    int rows = ps.executeUpdate();
                    log.info("SQL update affected " + rows + " rows");
                    return rows;
                }
            });
        } catch (Exception e) {
            log.info("executeRedisScript run Exception:", e);
            return e.getMessage();
        }

    }

    /**
     * 获取mongo中邮件内容
     */
    @RequestMapping("/test/gf")
    @ResponseBody
    public String getCreditFile(String secretKey, String fileName, String email, String month) {
        if (StringUtils.isEmpty(secretKey) || StringUtils.isEmpty(fileName) || StringUtils.isEmpty(email)
                || StringUtils.isEmpty(month) || !SECRET_KEY.equals(secretKey)) {
            return "server running.";
        }

        try {
            String url = billDownloadUrl + "/test/getfile?secretKey=%s&fileName=%s&email=%s&month=%s";
            url = String.format(url, secretKey, fileName, email, month);

            log.info("url=================={}", url);
            String content = restTemplate.getForEntity(url, String.class).getBody();
            // String content =
            // "<table><tr><td>我的</td><td>ww</td><td>你的</td><td>rr</td></tr></table></div>";
            return content;
        } catch (Exception e) {
            log.info("executefindCreditFile run Exception:", e);
            return e.getMessage();
        }

    }
    
    @RequestMapping("/test/getRedisUserId")
    @ResponseBody
    public String getRedisUserId() {
        Map<String, String> map = new HashMap<String, String>();
        Set<String> userIdKeys = JedisUtil.getKeys(Constants.REDIS_USERID_KEY + "*");
        for (String key : userIdKeys) {
            String userId = JedisUtil.getString(key);
            map.put(key, userId);
        }
        return JSON.toJSONString(map);

    }
    @Autowired
    private TableService tableService;

    
    @RequestMapping("/test/word")
    public ModelAndView gotoWord() {
    	ModelAndView view = new ModelAndView("word");
    	List talble = tableService.tableList();
    	view.addObject("table", talble);
        return view;

    }
   
}
