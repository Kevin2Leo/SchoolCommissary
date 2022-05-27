package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 项目的启动类
 * @Date Created in 23:31 2022/5/20
 * @Author: Chen_zhuo
 * @Modified By
 */

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement //开启事务注解支持
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功!!!");
    }

}
