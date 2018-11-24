package com.itheima.pyg.service.seller;

import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.seller.Seller;

public interface SellerService {
    /**
     * 商家入驻
     * @param seller
     * @return
     */
    Result add(Seller seller);

    /**
     * 查询待审核商家
     * @param pageNum
     * @param pageSize
     * @param seller
     * @return
     */
    PageResult search(Integer pageNum, Integer pageSize, Seller seller);

    /**
     * 查询待审核商家实体对象
     * @param id
     * @return
     */
    Seller findOne(String id);

    /**
     * 更新商家审核状态
     * @param sellerId
     * @param status
     * @return
     */
    Result updateStatus(String sellerId, String status);
}
