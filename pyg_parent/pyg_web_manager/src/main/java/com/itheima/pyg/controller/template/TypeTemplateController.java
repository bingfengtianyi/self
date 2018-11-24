package com.itheima.pyg.controller.template;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.template.TypeTemplate;
import com.itheima.pyg.service.template.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    /**
     * 带条件的分页查询
     * @param pageNum
     * @param pageSize
     * @param typeTemplate
     * @return
     */
    @RequestMapping("search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody TypeTemplate typeTemplate){
        return typeTemplateService.search(pageNum,pageSize,typeTemplate);
    }

    /**
     * 添加模板
     * @param typeTemplate
     * @return
     */
    @RequestMapping("add")
    public Result   add(@RequestBody TypeTemplate typeTemplate){
        return typeTemplateService.add(typeTemplate);
    }

    /**
     * 更新模板
     * @param typeTemplate
     * @return
     */
    @RequestMapping("update")
    public Result   update(@RequestBody TypeTemplate typeTemplate){
        return typeTemplateService.update(typeTemplate);
    }

    /**
     * 查询模板实体对象
     * @return
     */
    @RequestMapping("findOne")
    public TypeTemplate findOne(long id){
        return typeTemplateService.findOne(id);
    }

    /**
     * 删除模板
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    public Result   delete(long[] ids){
        return typeTemplateService.delete(ids);
    }
}

