package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Date Created in 21:37 2022/5/24
 * @Author: Chen_zhuo
 * @Modified By
 */
@Data
public class OrdersDto extends Orders {

//    private String userName;
//
//    private String phone;
//
//    private String address;

//    private String consignee;

    private List<OrderDetail> orderDetails;

}

