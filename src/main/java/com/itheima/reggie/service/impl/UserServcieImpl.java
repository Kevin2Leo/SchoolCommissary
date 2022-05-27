package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Date Created in 15:11 2022/5/23
 * @Author: Chen_zhuo
 * @Modified By
 */
@Service
public class UserServcieImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
