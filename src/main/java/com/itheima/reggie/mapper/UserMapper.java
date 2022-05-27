package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Date Created in 15:10 2022/5/23
 * @Author: Chen_zhuo
 * @Modified By
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
