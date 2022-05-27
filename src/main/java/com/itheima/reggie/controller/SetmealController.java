package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Date Created in 21:35 2022/5/22
 * @Author: Chen_zhuo
 * @Modified By
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name) {
        //1.构造分页构造器
        Page<Setmeal> pageInfo = new Page(page, pageSize);
        Page<SetmealDto> pageDtoInfo = new Page(page, pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper();
        //添加一个name条件 可能为空
        wrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        //添加一个未删除条件（逻辑删除）
        wrapper.eq(Setmeal::getIsDeleted, 0);
        //添加排序条件
        wrapper.orderByDesc(Setmeal::getUpdateTime);

        //3.执行查询
        setmealService.page(pageInfo, wrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records");

        List<Setmeal> setmealList = pageInfo.getRecords();
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        for (Setmeal setmeal : setmealList) {

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);

            setmealDtoList.add(setmealDto);
        }
        pageDtoInfo.setRecords(setmealDtoList);
        return R.success(pageDtoInfo);
    }

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {

        setmealService.save(setmealDto);//直接先保存setmeal的数据
        //该过程会自动生成setmealId
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDisheList = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDisheList) {
            setmealDish.setSetmealId(setmealId);
        }
        //批量保存 saveBatch
        setmealDishService.saveBatch(setmealDisheList);
        return R.success("添加套餐成功");
    }

    /**
     * 删除套餐 及 套餐关联的菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {

        setmealService.removeWithDish(ids);

        return R.success("套餐删除成功");
    }

    /**
     * 启售 或 停售
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {

        for (Long id : ids) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("操作成功");
    }

    /**
     *  前台套餐展示
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(Setmeal::getStatus, 1)//status==1 起售
                .orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmealList = setmealService.list(wrapper);
        List<SetmealDto> setmealDtoList = new ArrayList<>();

        for (Setmeal setmeal1 : setmealList) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal1,setmealDto);
            Long setmealId = setmeal1.getId();
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
            List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(setmealDishList);
            setmealDtoList.add(setmealDto);
        }

        return R.success(setmealDtoList);
    }

}
