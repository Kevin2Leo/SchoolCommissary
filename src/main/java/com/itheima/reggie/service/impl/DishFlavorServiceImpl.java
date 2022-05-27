package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Date Created in 17:47 2022/5/22
 * @Author: Chen_zhuo
 * @Modified By
 */
@Service
public class DishFlavorServiceImpl
        extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}