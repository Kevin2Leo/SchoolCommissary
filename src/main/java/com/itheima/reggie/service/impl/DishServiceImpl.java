package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Date Created in 11:15 2022/5/22
 * @Author: Chen_zhuo
 * @Modified By
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品同时新增口味数据
     * @param dishDto
     */
    @Transactional //涉及到多张表 要加上事务控制
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        this.save(dishDto);//经过这一步，dish里面自动生成有了dishId
                           //并返回到dishDto中
        Long dishId = dishDto.getId();
        List<DishFlavor> flavorList = dishDto.getFlavors();

        for (DishFlavor dishFlavor : flavorList) {
            dishFlavor.setDishId(dishId);
        }
        //批量保存 saveBatch
        dishFlavorService.saveBatch(flavorList);
    }
}
