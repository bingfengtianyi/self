package com.itheima.pyg.controller.itemcat;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.item.ItemCat;
import com.itheima.pyg.service.itemcat.ItemCatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    /**
     * 根据父ID查询分类列表
     * @param parentId
     * @return
     */
    @RequestMapping("findByParentId")
    public List<ItemCat>    findByParentId(long parentId){
        return itemCatService.findByParentId(parentId);
    }

    /**
     * 运营商查询所有商品分类信息
     * @return
     */
    @RequestMapping("findAll")
    public List<ItemCat>    findAll(){
        return itemCatService.findAll();
    }
}
