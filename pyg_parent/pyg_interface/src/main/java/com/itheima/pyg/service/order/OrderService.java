package com.itheima.pyg.service.order;

import com.itheima.pyg.pojo.log.PayLog;
import com.itheima.pyg.pojo.order.Order;

import java.util.List;

public interface OrderService {

    void add(Order order);

    List<Order> findOrderListByUserId(String userId);

    PayLog findPayLogByUserIdFromRedis(String userId);

    void updateOrderStatus(String out_trade_no,String transaction_id);

}
