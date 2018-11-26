package com.itheima.pyg.service.cart;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.pyg.dao.item.ItemDao;
import com.itheima.pyg.entity.vo.Cart;
import com.itheima.pyg.pojo.item.Item;
import com.itheima.pyg.pojo.order.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加商品至购物车
     *
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据sku id查询商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("该商品不存在");
        }
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("该商品已下架");
        }
        //2.根据商品信息获得商家id
        String sellerId = item.getSellerId();
        //3.判断购物车列表中 商家是否存在(写独立方法)
        Cart cart = searchCartBySellerId(cartList, sellerId);
        if (cart == null) {
            //4如果商家不存在,新建购物车对象,新建购物车明细对列表,依次存入
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //写独立方法创建订单项对象
            OrderItem orderItem = createOrderItem(item, num);
            List<OrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //将新创建的购物车对象添加到购物车列表中
            cartList.add(cart);
        } else {
            //5.1如果商家存在,判断该商家购物车明细列表中是否存在此sku,不存在则添加(写独立方法)
            OrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (orderItem == null) {
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            } else {
                //5.2商家及sku均存在,则修改其数量和小计金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }
                if (cart.getOrderItemList().size() <= 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    /**
     * 从redis中获取购物车列表
     *
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList==null){
            cartList = new ArrayList<>();
        }
        System.out.println("从redis中提取购物车数据 "+username);
        return cartList;
    }

    /**
     * 向redis中存储购物车
     *
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username, cartList);
        System.out.println("向redis中存储购物车列表数据 "+username);
    }

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        //实现思路：循环一个购物车 ，根据购物车中的商品ID和数量 添加到另一个购物中
        for(Cart cart:cartList2 ){
            for(OrderItem orderItem:cart.getOrderItemList()){
                cartList1= addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList1;
    }

    /**
     * 根据商家id判断购物车列表中是否存在该商家的购物车对象
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }
        return null;
    }

    /**
     * 根据sku信息 创建购物车明细列表项
     *
     * @param item
     * @param num
     * @return
     */
    private OrderItem createOrderItem(Item item, Integer num) {
        if (num <= 0) {
            num = 1;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    /**
     * 根据sku id查询购物车明细列表中是否有此sku
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {
        if (orderItemList != null && orderItemList.size() > 0) {
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().longValue() == itemId.longValue()) {
                    return orderItem;
                }
            }
        }
        return null;
    }

}
