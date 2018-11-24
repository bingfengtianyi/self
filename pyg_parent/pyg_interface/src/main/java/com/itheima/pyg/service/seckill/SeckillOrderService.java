package com.itheima.pyg.service.seckill;

import com.itheima.pyg.pojo.seckill.SeckillOrder;

/**
 * 秒杀商品订单接口
 */
public interface SeckillOrderService {
    /**
     * 提交秒杀订单
     * @param seckillId
     * @param userId
     */
    void submitOrder(long seckillId,String userId);

    /**
     * 从缓存中提取订单
     * @param userId
     * @return
     */
    SeckillOrder searchOrderFromRedis(String userId);

    /**
     * 支付成功后,将redis中的订单同步到数据库,并清除redis中的订单
     * @param userId
     * @param orderId
     * @param transactionId
     */
    void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);

    /**
     * 订单超时后从缓存中清除订单,回复库存
     * @param userId
     * @param orderId
     */
    void deleteOrderFromRedis(String userId,Long orderId);
}
