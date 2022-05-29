package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Date Created in 15:06 2022/5/22
 * @Author: Chen_zhuo
 * @Modified By
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

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
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> pageDtoInfo = new Page(page, pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper();
        //添加一个name条件 可能为空
        wrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //添加一个未删除条件（逻辑删除）
        wrapper.eq(Dish::getIsDeleted, 0);
        //添加排序条件
        wrapper.orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        //3.执行查询
        dishService.page(pageInfo, wrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records");

        List<Dish> dishList = pageInfo.getRecords();
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish dish : dishList) {

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            dishDtoList.add(dishDto);
        }
        pageDtoInfo.setRecords(dishDtoList);
        return R.success(pageDtoInfo);
    }

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        dishService.saveWithFlavor(dishDto);
        //数据库改变后，要删除Redis中相应的缓存
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("添加菜品成功!");
    }

    /**
     * 根据菜品id删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        for (Long id : ids) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setIsDeleted(1);
            dishService.updateById(dish);//逻辑删除，把is_deleted的数值改为1
            //数据库改变后，要删除Redis中相应的缓存
            Dish dish1 = dishService.getById(id);
            String key = "dish_" + dish1.getCategoryId() + "_" + dish1.getStatus();
            redisTemplate.delete(key);
        }

        return R.success("删除菜品成功");
    }

    /**
     * 根据菜品id查询菜品及口味 具体信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        Dish dish = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper();
        wrapper.eq(DishFlavor::getDishId, id);

        List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
        dishDto.setFlavors(dishFlavorList);

        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateById(dishDto);//因为DishDto是Dish的子类

        //对于口味，采用先删后加的方式
        Long dishId = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(queryWrapper);//先删除

        List<DishFlavor> flavorList = dishDto.getFlavors();
        dishFlavorService.saveBatch(flavorList);//再添加

        //数据库改变后，要删除Redis中相应的缓存
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        List<DishDto> dishDtoList = null;
        //动态设置Redis缓存中的key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        //优化：先从Redis中获取菜品缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果缓存中有数据,则直接返回
        if (dishDtoList != null) {
            return R.success(dishDtoList);
        }

        //如果缓存中没有数据,则查询数据库,并把查询结果缓存在Redis中
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .like(dish.getName() != null, Dish::getName, dish.getName())
                .eq(Dish::getStatus, 1)//status==1 起售
                .eq(Dish::getIsDeleted,0)//逻辑删除 0
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);
        dishDtoList = new ArrayList<>();

        for (Dish dish1 : dishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);//拷贝一份

            Long dishId = dish1.getId();
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFlavors);

            dishDtoList.add(dishDto);
        }
        //将查询结果缓存到数据库中
        redisTemplate.opsForValue().set(key, dishDtoList, 60L, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

    /**
     * 启售 或 停售
     *
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {

        for (Long id : ids) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            dishService.updateById(dish);

            //数据库改变后，要删除Redis中相应的缓存
            Dish dish1 = dishService.getById(id);
            String key = "dish_" + dish1.getCategoryId() + "_" + dish1.getStatus();
            redisTemplate.delete(key);
        }


        return R.success("操作成功");
    }
}