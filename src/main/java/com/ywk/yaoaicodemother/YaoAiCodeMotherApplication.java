package com.ywk.yaoaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ywk.yaoaicodemother.mapper")
public class YaoAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(YaoAiCodeMotherApplication.class, args);
    }

}
