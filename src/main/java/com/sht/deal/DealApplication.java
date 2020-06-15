package com.sht.deal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.sht.deal.Mapper")
@EnableScheduling
public class DealApplication {
    public static void main(String[] args)  {
        SpringApplication.run(DealApplication.class, args);
    }
}
