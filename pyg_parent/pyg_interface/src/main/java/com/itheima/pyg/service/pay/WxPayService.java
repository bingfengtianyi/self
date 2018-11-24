package com.itheima.pyg.service.pay;

import java.util.Map;

/**
 * 微信支付接口
 */
public interface WxPayService {

    /**
     * 生成微信支付二维码,并显示订单号和总金额
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    Map createNative(String out_trade_no,String total_fee);

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
    Map queryPayStatus(String out_trade_no);

    /**
     * 轮询订单支付状态
     * @param out_trade_no
     * @return
     */
    Map queryPayStatusWhile(String out_trade_no);
}
