package com.chaochaoyuanzi.gaochao_usercenter_back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chaochaoyuanzi.gaochao_usercenter_back.mapper")
public class GaoChaoUserCenterBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaoChaoUserCenterBackApplication.class, args);
    }

}
