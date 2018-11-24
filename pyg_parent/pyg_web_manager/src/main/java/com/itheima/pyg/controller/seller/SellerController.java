package com.itheima.pyg.controller.seller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.PageResult;
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
     * 查询待审核商家
     * @param pageNum
     * @param pageSize
     * @param seller
     * @return
     */
    @RequestMapping("search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody Seller seller){
        return sellerService.search(pageNum,pageSize,seller);
    }

    /**
     * 查询待审核商家实体对象
     * @param id
     * @return
     */
    @RequestMapping("findOne")
    public Seller   findOne(String id){
        return sellerService.findOne(id);
    }

    /**
     * 更新商家审核状态
     * @param sellerId
     * @param status
     * @return
     */
    @RequestMapping("updateStatus")
    public Result   updateStatus(String sellerId,String status){
        return sellerService.updateStatus(sellerId,status);
    }
}
