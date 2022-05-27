package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

/**
 * @Description:
 * @Date Created in 11:14 2022/5/22
 * @Author: Chen_zhuo
 * @Modified By
 */
public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);
}
