package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * @Description:
 * @Date Created in 11:15 2022/5/22
 * @Author: Chen_zhuo
 * @Modified By
 */
public interface SetmealService extends IService<Setmeal> {


    /**
     * 删除套餐 及 套餐关联数据
     * 如果status==1 表示在售 则不能删除
     * @param ids
     */
    void removeWithDish(List<Long> ids);


}
