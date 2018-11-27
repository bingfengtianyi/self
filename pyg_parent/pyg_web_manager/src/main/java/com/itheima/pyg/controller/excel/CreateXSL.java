package com.itheima.pyg.controller.excel;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.good.Goods;
import com.itheima.pyg.pojo.user.User;
import com.itheima.pyg.service.goods.GoodsService;
import com.itheima.pyg.service.order.OrderService;
import com.itheima.pyg.util.ExcelUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("createXSL")
public class CreateXSL {

    @Reference
    private GoodsService goodsService;

    @Reference
    private OrderService orderService;

    /**
     * 运营商后台 商品列表导出为excel
     * @param response
     */
    @RequestMapping("exportexcelForGoods")
    public void exportexcelForGoods(HttpServletResponse response) {
        List<Goods> userList = goodsService.getGoodList();
        String[] columnNames = {
                "商家ID","SPU名","默认SKU","状态","是否上架","品牌","副标题","一级类目","二级类目",
                "三级类目","小图","商城价","分类模板ID","是否启用规格","是否删除"
        };
        String[] columns = {
                "sellerId","goodsName","defaultItemId","auditStatus","isMarketable","brandId",
                "caption","category1Id","category2Id","category3Id","smallPic","price","typeTemplateId",
                "isEnableSpec","isDelete"
        };
        ExcelUtils.exportExcel(response,userList,columnNames,columns,"商品表","E:\\Goods.html");
    }

    /**
     * 运营商后台 订单数据导出为excel
     * @param response
     */
    @RequestMapping("exportexcelForOrders")
    public void exportexcelForOrders(HttpServletResponse response) {
        List<Goods> userList = goodsService.getGoodList();
        String[] columnNames = {
                "订单id","实付金额","支付类型","邮费","状态","订单创建时间","订单更新时间","付款时间",
                "发货时间","交易完成时间","交易关闭时间","物流名称","物流单号","用户id","买家留言",
                "买家昵称","买家是否已经评价","收货人地区名称","收货人手机","收货人邮编","收货人",
                "过期时间","发票类型","订单来源","商家ID","支付订单号"
        };
        String[] columns = {
                "orderId","payment","paymentType","postFee","status","createTime","updateTime",
                "paymentTime","consignTime","endTime","closeTime","shippingName","shippingCode",
                "userId","buyerMessage","buyerNick","buyerRate","receiverAreaName","receiverMobile",
                "receiverZipCode","receiver","expire","invoiceType","sourceType","sellerId","outTradeNo"
        };
        ExcelUtils.exportExcel(response,userList,columnNames,columns,"订单表","E:\\Orders.html");
    }

}
