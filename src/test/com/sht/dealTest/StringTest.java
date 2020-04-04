package com.sht.dealTest;

import com.sht.deal.utils.JwtUtils;
import org.junit.Test;

/**
 * @author Sht
 * @project deal
 * @date 2020/3/15 - 18:24
 **/

public class StringTest {
    @Test
    public void stringTest(){
        String url = "http://47.93.240.205:8800/deal/goods/684e7970-1614-484d-85d3-bb0e48d61831thumbnail.jpg";
        String path = url.substring(25); //去掉http://47.93.240.205:8800
        System.out.println("11111111111 " + path);
        String name2 = path.substring(0, path.indexOf("thumbnail"));
        String jpg = url.substring(url.lastIndexOf("."));
        String path2 = name2 + jpg;
        System.out.println("22222222222 " + path2);
    }

    @Test
    public void tokenTest(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzaHREZWFsIiwiaWQiOjMsInVzZXJuYW1lIjoic2h0MTIzIiwiaW1nIjoiaHR0cDovLzM5LjEwNi4xODguMjI6ODgwMC9kZWFsL3VzZXIvNTJhYzJjMzktOTI4Mi00NjI2LWFiOWUtNGU0MmQ3Yjk0YWEzLnBuZyIsImlhdCI6MTU3NzE4ODk5NywiZXhwIjoxNTc3NjIwOTk3fQ.Aaw8j096fJ80vpo6r6gZ3E2liqzYy8sm_s_FDBW-BvQ";
        Object o = JwtUtils.checkJWT(token);
        System.out.println(o);
    }
}
