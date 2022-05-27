package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;



/**
 * @Description:
 * @Date Created in 0:00 2022/5/21
 * @Author: Chen_zhuo
 * @Modified By
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
