/**
 * @Title: GlobalControllerExceptionHandler.java
 * @Package com.pay.card.Exception
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年12月7日
 * @version V1.0
 */

package com.pay.card.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.enums.CodeEnum;
import com.pay.card.view.JsonResultView;

/**
 * @ClassName: GlobalControllerExceptionHandler
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月7日
 */
@ControllerAdvice
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(value = { UserNotFoundException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonResultView constraintViolationException(UserNotFoundException ex) {
        return new JsonResultView(CodeEnum.PARAMETER_REEOR).setObject(new JSONObject());
    }

    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonResultView unknownException(Exception ex) {
        logger.error(ex.getMessage());
        return new JsonResultView(CodeEnum.FAIL);
    }
}
