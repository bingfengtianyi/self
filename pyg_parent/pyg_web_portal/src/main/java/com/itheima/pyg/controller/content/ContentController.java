package com.itheima.pyg.controller.content;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.ad.Content;
import com.itheima.pyg.service.content.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("content")
public class ContentController {

    @Reference
    private ContentService contentService;

    /**
     * 根据广告分类id查询广告列表
     * @param categoryId
     * @return
     */
    @RequestMapping("findByCategoryId")
    public List<Content>    findByCategoryId(long categoryId){
        return contentService.findByCategoryId(categoryId);
    }
}
