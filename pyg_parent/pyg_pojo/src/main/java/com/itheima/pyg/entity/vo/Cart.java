package com.itheima.pyg.entity.vo;

import com.itheima.pyg.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

public class Cart   implements Serializable {

    private String  sellerId;
    private String  sellerName;
    private List<OrderItem> orderItemList;

    public Cart() {
    }

    public Cart(String sellerId, String sellerName, List<OrderItem> orderItemList) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.orderItemList = orderItemList;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
