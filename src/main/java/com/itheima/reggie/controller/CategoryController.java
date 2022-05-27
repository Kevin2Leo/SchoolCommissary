package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Date Created in 17:26 2022/5/21
 * @Author: Chen_zhuo
 * @Modified By
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize) {

        //1.构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //2.构造条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper();
        //添加一个未删除条件
        wrapper.eq(Category::getIsDeleted, 0);
        //添加一个排序条件
        wrapper.orderByAsc(Category::getSort);
        //3.执行查询
        categoryService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }

    /**
     * 新增菜品分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Category category) {

        categoryService.save(category);

        return R.success("分类添加成功");
    }

    /**
     * 修改菜品分类
     *
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {

        categoryService.updateById(category);

        return R.success("修改分类成功");
    }

    /**
     * 根据id删除菜品分类，此次我采用的是软删除
     * 需要考虑该菜品分类下有没有关联菜品或者套餐，如果关联了，就不删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        Category category = new Category();
        category.setId(ids);
        category.setIsDeleted(1);
        categoryService.myLogicDelete(category);//逻辑删除 把is_deleted改为1
        return R.success("分类信息删除成功");
    }

    /**
     * 新增菜品时的 分类列表
     *
     * @param type
     * @return
     */

    @GetMapping("/list")
    public R<List<Category>> list(Category category) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType())
                    .orderByAsc(Category::getSort)
                    .orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(queryWrapper);

        return R.success(categoryList);
    }
}
