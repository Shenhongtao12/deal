package com.sht.dealTest;

import com.github.pagehelper.PageInfo;
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
}
