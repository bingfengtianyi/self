package com.itheima.pyg.service.goods;

import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.entity.vo.GoodsVo;
import com.itheima.pyg.pojo.good.Goods;
import com.itheima.pyg.pojo.item.Item;
import com.itheima.pyg.pojo.order.Order;

import java.util.List;

public interface GoodsService {
    /**
     * 运营商后台 查询全部商品,用于导出excel
     * @return
     */
    List<Goods> getGoodList();

    /**
     * 商品录入
     * @param goodsVo
     * @return
     */
    void add(GoodsVo goodsVo);

    /**
     * 分页查询商品列表
     * @param pageNum
     * @param pageSize
     * @param goods
     * @return
     */
    PageResult search(Integer pageNum, Integer pageSize, Goods goods);

    /**
     * 商品数据回显
     * @param id
     * @return
     */
    GoodsVo findOne(long id);

    /**
     * 更新商品数据
     * @param goodsVo
     */
    void update(GoodsVo goodsVo);

    /**
     * 运营商查询全部待审核商品
     * @param pageNum
     * @param pageSize
     * @param goods
     * @return
     */
    PageResult searchForManager(Integer pageNum, Integer pageSize, Goods goods);

    /**
     * 运营商进行商品审核,即修改商品状态
     * @param ids
     * @param status
     */
    void updateStatus(long[] ids, String status);

    /**
     * 逻辑删除待审核商品
     * @param ids
     */
    void delete(long[] ids);

    /**
     * 分页获取商品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult<Goods> getGoodsListByPage(Integer pageNum, Integer pageSize);

    /**
     * 根据商品id查询库存列表
     * @return
     */
    List<Item> findItemList(long[] ids);

    List<String> findItemIds(long[] ids);
}
