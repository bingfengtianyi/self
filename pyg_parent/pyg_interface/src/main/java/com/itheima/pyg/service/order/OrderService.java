package com.itheima.pyg.service.order;

import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.pojo.log.PayLog;
import com.itheima.pyg.pojo.order.Order;
import com.itheima.pyg.pojo.user.User;

import java.util.List;

public interface OrderService {

    void add(Order order);

    List<Order> findOrderListByUserId(String userId);

    PayLog findPayLogByUserIdFromRedis(String userId);

    void updateOrderStatus(String out_trade_no,String transaction_id);

    //查出未付款订单

    List<Order> findOrderListByUserIdUnPay(String userId);
    /**
     * 运营商后台 查询全部订单数据用于导出excel
     * @return
     */
    List<Order> getOrderList();


    PageResult<Order> getOrderListByPage(Integer pageNum, Integer pageSize);
}
