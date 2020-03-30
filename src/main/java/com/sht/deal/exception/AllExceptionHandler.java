package com.sht.deal.exception;

import com.sht.deal.utils.JsonData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理控制器
 */
//@ControllerAdvice
public class AllExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonData Handler(Exception e){

        if (e instanceof AllException) {
            AllException allException = (AllException) e;
            return JsonData.buildError(allException.getMsg(), allException.getCode());
        }else {
            return JsonData.buildError("未知错误！");
        }
    }

}
