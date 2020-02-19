package com.sht.deal.exception;

import lombok.Data;

@Data
public class AllException extends RuntimeException {

    private Integer code;  //状态码

    private String msg;  //异常消息

    public AllException (Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
