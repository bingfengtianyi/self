package com.itheima.pyg.controller.seckillGoodsApp;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.seckill.SeckillGoods;
import com.itheima.pyg.service.seckill.SeckillGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("seckillGoods")
public class SeckillGoodsAppController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询参与秒杀活动的商品列表
     * @return
     */
    @RequestMapping("findList")
    public List<SeckillGoods> findList(){
        return seckillGoodsService.findSeckillGoodListFromDB();
    }
}
