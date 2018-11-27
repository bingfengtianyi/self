package com.itheima.pyg.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.pojo.order.Order;
import com.itheima.pyg.pojo.user.User;
import com.itheima.pyg.service.order.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("findOrderList")
    public PageResult<Order> findOrderList(Integer pageNum, Integer pageSize){
        return orderService.getOrderListByPage(pageNum,pageSize);
    }
}
