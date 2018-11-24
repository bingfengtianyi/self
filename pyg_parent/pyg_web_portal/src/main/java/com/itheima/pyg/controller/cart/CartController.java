package com.itheima.pyg.controller.cart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.LoginResult;
import com.itheima.pyg.entity.vo.Cart;
import com.itheima.pyg.service.cart.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {
    @Reference(timeout = 6000)
    private CartService cartService;

    /**
     * 添加商品至购物车列表
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("addGoodsToCartList")
    public LoginResult addGoodsToCartList(@RequestBody List<Cart> cartList,Long itemId, Integer num){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户名:"+name);
        if ("anonymousUser".equals(name)){
            name = "";
        }
        try {
            if (!"".equals(name)){//如果用户已登录,则向redis中存储一份
                List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
                List<Cart> carts = cartService.addGoodsToCartList(cartListFromRedis, itemId, num);
                cartService.saveCartListToRedis(name,carts);
                return new LoginResult(true,name,carts);
            }else {
                List<Cart> carts = cartService.addGoodsToCartList(cartList, itemId, num);
                return new LoginResult(true,name,carts);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false,name,"添加商品失败");
        }
    }

    /**
     * 查找购物车,主要作用是判断用户是否登录
     * @return
     */
    @RequestMapping("findCartList")
    public LoginResult findCartList(@RequestBody List<Cart>  cartList){
        //从springSecurity中获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前用户名 "+username);
        if ("anonymousUser".equals(username)){//如果用户没有登录,直接把前端传过来的购物车列表返回
            username = "";
            return new LoginResult(true,username,cartList);
        }else {//如果用户已登录,从redis中提取购物车列表,然后返回
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);
            if (cartList.size()>0){
                cartListFromRedis = cartService.mergeCartList(cartList, cartListFromRedis);
                cartService.saveCartListToRedis(username,cartListFromRedis);
            }
            return new LoginResult(true,username,cartListFromRedis);
        }
    }
}
