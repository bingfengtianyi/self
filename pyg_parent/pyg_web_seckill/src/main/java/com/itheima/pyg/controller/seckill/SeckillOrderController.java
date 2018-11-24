package com.itheima.pyg.controller.seckill;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.service.seckill.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 提交秒杀订单
     * @param seckillId
     * @return
     */
    @RequestMapping("submitOrder")
    public Result   submitOrder(long seckillId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)){
            return new Result(false,"请登录系统");
        }
        try {
            seckillOrderService.submitOrder(seckillId,username);
            return new Result(true,"提交订单成功");
        } catch (RuntimeException e){
            return new Result(false,e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交订单失败");
        }
    }


}
