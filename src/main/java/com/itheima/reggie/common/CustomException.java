package com.itheima.reggie.common;

/**
 * @Description: 自定义业务异常
 * @Date Created in 14:53 2022/5/22
 * @Author: Chen_zhuo
 * @Modified By
 */
public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }

}
