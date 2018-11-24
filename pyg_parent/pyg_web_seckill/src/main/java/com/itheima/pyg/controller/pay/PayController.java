package com.itheima.pyg.controller.pay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.seckill.SeckillOrder;
import com.itheima.pyg.service.pay.WxPayService;
import com.itheima.pyg.service.seckill.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("pay")
public class PayController {

    @Reference
    private WxPayService wxPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("createNative")
    public Map  createNative(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)){
            return new HashMap();
        }
        SeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedis(username);
        if (seckillOrder!=null){
            long money = (long) (seckillOrder.getMoney().doubleValue()*100);
            Map map = wxPayService.createNative(seckillOrder.getId() + "", money + "");
            return map;
        }else {
            return new HashMap();
        }
    }

    @RequestMapping("queryPayStatus")
    public Result queryOrderStatus(String out_trade_no){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,String> map = wxPayService.queryPayStatusWhile(out_trade_no);
        if (map==null){
            return new Result(false,"支付失败");
        }else {
            if ("SUCCESS".equals(map.get("trade_state"))){
                try {
                    seckillOrderService.saveOrderFromRedisToDb(userId,Long.valueOf(out_trade_no),map.get("transaction_id"));
                    return new Result(true,"支付成功");
                } catch (RuntimeException e){
                    e.printStackTrace();
                    return new Result(false,e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Result(false,"支付失败");
                }
            }else {
                seckillOrderService.deleteOrderFromRedis(userId,Long.valueOf(out_trade_no));
                return new Result(false,"二维码超时");
            }
        }
    }
}
