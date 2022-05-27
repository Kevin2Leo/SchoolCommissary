package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * @Description:
 * @Date Created in 17:28 2022/5/21
 * @Author: Chen_zhuo
 * @Modified By
 */
public interface CategoryService extends IService<Category> {

    /*
     *自定义的逻辑删除方法
     */
    void myLogicDelete(Category category);

}
