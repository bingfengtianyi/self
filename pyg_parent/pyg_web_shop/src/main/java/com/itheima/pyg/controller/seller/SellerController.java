package com.itheima.pyg.controller.seller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.seller.Seller;
import com.itheima.pyg.service.seller.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 商家入驻
     * @param seller
     * @return
     */
    @RequestMapping("add")
    public Result   add(@RequestBody Seller seller){
        return sellerService.add(seller);
    }

}
