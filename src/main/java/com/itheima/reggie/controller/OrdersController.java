package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date Created in 21:15 2022/5/24
 * @Author: Chen_zhuo
 * @Modified By
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {

        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 订单详情
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(Integer page, Integer pageSize) {
        //获得当前用户的id
        Long userId = BaseContext.getCurrentId();

        Page<Orders> pageInfo = new Page(page, pageSize);
        Page<OrdersDto> pageDtoInfo = new Page(page, pageSize);

        //获得当前用户的订单数据
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Orders::getUserId, userId);
        ordersService.page(pageInfo, wrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records");

        List<Orders> ordersList = pageInfo.getRecords();

        List<OrdersDto> ordersDtoList = new ArrayList<>();

        for (Orders orders : ordersList) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);//拷贝一份

            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, orders.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
            ordersDto.setOrderDetails(orderDetails);
            ordersDtoList.add(ordersDto);
        }
        pageDtoInfo.setRecords(ordersDtoList);
        return R.success(pageDtoInfo);
    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        //1.构造分页构造器
        Page<Orders> pageInfo = new Page(page, pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper();
        //添加一个number条件(订单号) 可能为空
        wrapper.like(number != null, Orders::getNumber, number);

        //添加时间条件
        wrapper.between(beginTime!=null || endTime != null, Orders::getOrderTime, beginTime, endTime);
        //3.执行查询
        ordersService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> order(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("派送成功");
    }
}
