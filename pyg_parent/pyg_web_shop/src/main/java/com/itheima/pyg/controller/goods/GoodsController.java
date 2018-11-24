package com.itheima.pyg.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.entity.vo.GoodsVo;
import com.itheima.pyg.pojo.good.Goods;
import com.itheima.pyg.service.goods.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 商品录入
     * @param goodsVo
     * @return
     */
    @RequestMapping("add")
    public Result add(@RequestBody GoodsVo goodsVo){
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsVo.getGoods().setSellerId(sellerId);
            goodsService.add(goodsVo);
            return new Result(true,"添加商品成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加商品失败");
        }
    }

    /**
     * 分页查询商品列表
     * @param pageNum
     * @param pageSize
     * @param goods
     * @return
     */
    @RequestMapping("search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody Goods goods){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(pageNum,pageSize,goods);
    }

    /**
     * 商品数据回显
     * @param id
     * @return
     */
    @RequestMapping("findOne")
    public GoodsVo  findOne(long id){
        return goodsService.findOne(id);
    }

    @RequestMapping("update")
    public Result update(@RequestBody GoodsVo goodsVo){
        try {
            goodsService.update(goodsVo);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

}
