package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @Description: 全局异常处理器
 * @Date Created in 10:10 2022/5/21
 * @Author: Chen_zhuo
 * @Modified By
 */
@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {

    //SQL完整性约束违反异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());
        String message = "未知错误";
        if (ex.getMessage().contains("Duplicate entry")) {//重复条目
            String[] split = ex.getMessage().split(" ");
            message = split[2] + "已经存在";
        }
        return R.error(message);
    }

    //自定义业务异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {

        return R.error(ex.getMessage());
    }


}
