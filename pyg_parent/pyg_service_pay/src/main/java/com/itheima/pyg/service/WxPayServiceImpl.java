package com.itheima.pyg.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.itheima.pyg.service.pay.WxPayService;
import com.itheima.pyg.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WxPayServiceImpl implements WxPayService {

    /**
     * 微信公众号id
     */
    @Value("${appid}")
    private String appid;

    /**
     * 商户账号
     */
    @Value("${partner}")
    private String partner;

    /**
     * 商户密钥
     */
    @Value("${partnerkey}")
    private String partnerkey;


    /**
     * 生成二维码,并显示订单号和总金额
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("body","品优购");
        paramMap.put("out_trade_no",out_trade_no);
        paramMap.put("total_fee",total_fee);
        paramMap.put("spbill_create_ip","127.0.0.1");
        paramMap.put("notify_url","http://ntlias-stu.boxuegu.com");
        paramMap.put("trade_type","NATIVE");
        //2.发送请求
        try {
            String paramXML = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println(paramXML);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(paramXML);
            client.post();
            //3.获得结果
            String result = client.getContent();
            System.out.println(result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String,String> map = new HashMap<>();
            if ("SUCCESS".equals(resultMap.get("return_code"))&&"SUCCESS".equals(resultMap.get("result_code"))){
                map.put("code_url",resultMap.get("code_url"));
                map.put("out_trade_no",out_trade_no);
                map.put("total_fee",total_fee);
            }else {
                System.out.println("出错了!");
            }
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.构建参数
        Map<String,String> paramMap=new HashMap();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("out_trade_no",out_trade_no );
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        try {
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("查询订单状态请求参数："+paramXml);
            //2.发送请求
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            //3.获取结果
            String resultXml = httpClient.getContent();
            System.out.println("查询订单状态获取结果："+resultXml);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 轮询订单支付状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatusWhile(String out_trade_no) {
        int count = 0;
        Map<String,String>  map = null;
        while (true){
            count++;
            if (count>10){
                break;//5分钟之后不再轮询
            }
            map = queryPayStatus(out_trade_no);
            if (map==null){
                break;
            }
            if ("SUCCESS".equals(map.get("trade_state"))){//如果支付成功
                break;
            }

            //间隔三秒查询一次
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
