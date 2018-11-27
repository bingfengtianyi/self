package com.itheima.pyg.service.seckill;

import com.itheima.pyg.pojo.seckill.SeckillGoods;

import java.util.List;

/**
 * 秒杀商品接口
 */
public interface SeckillGoodsService {

    /**
     * 查询所有参与秒杀活动的商品列表
     * @return
     */
    List<SeckillGoods>  findSeckillGoodList();

    List<SeckillGoods>  findSeckillGoodListFromDB();

    /**
     * 根据id查询秒杀商品实体对象
     * @param id
     * @return
     */
    SeckillGoods findOneFromRedis(long id);
}
