package com.itheima.pyg.controller.seckill;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.seckill.SeckillGoods;
import com.itheima.pyg.service.seckill.SeckillGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询参与秒杀活动的商品列表
     * @return
     */
    @RequestMapping("findList")
    public List<SeckillGoods> findList(){
        return seckillGoodsService.findSeckillGoodList();
    }

    /**
     * 根据id查询秒杀商品实体对象
     * @param id
     * @return
     */
    @RequestMapping("findOneFromRedis")
    public SeckillGoods findOneFromRedis(long id){
        return seckillGoodsService.findOneFromRedis(id);
    }

}
