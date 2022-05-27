package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Date Created in 17:29 2022/5/21
 * @Author: Chen_zhuo
 * @Modified By
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id进行逻辑删除，删除之前需要进行判断
     * @param category
     */
    @Override
    public void myLogicDelete(Category category) {
        Long categoryId = category.getId();
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,categoryId);
        //查询当前菜品分类 是否关联菜品
        int count1 = dishService.count(dishQueryWrapper);
        if (count1 > 0){//如果已经关联 抛出一个业务异常
            throw new CustomException("当前过分类下关联了菜品,不能删除");
        }

        //查询当前菜品分类 是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper=new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,categoryId);
        int count2 = setmealService.count(setmealQueryWrapper);
        if (count2 > 0){//如果已经关联抛出一个业务异常
            throw new CustomException("当前过分类下关联了套餐,不能删除");
        }

        //没有任何关联 逻辑删除菜品分类
        super.updateById(category);
    }
}
