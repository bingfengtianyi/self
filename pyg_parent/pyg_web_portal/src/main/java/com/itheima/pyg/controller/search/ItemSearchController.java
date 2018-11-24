package com.itheima.pyg.controller.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.service.search.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("itemSearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 前台根据关键字及其他条件检索
     * @param searchMap
     * @return
     */
    @RequestMapping("search")
    public Map<String,Object> search(@RequestBody Map<String,String>    searchMap){
        return itemSearchService.search(searchMap);
    }
}
