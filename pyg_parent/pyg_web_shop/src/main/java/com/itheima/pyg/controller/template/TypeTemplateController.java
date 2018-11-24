package com.itheima.pyg.controller.template;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.pojo.template.TypeTemplate;
import com.itheima.pyg.service.template.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    /**
     * 查询模板实体对象,确定模板加载后的品牌和扩展属性列表
     * @param id
     * @return
     */
    @RequestMapping("findOne")
    public TypeTemplate findOne(long id){
        return  typeTemplateService.findOne(id);
    }

    /**
     * 根据模板id查询对应的规格和规格选项
     * @param id
     * @return
     */
    @RequestMapping("findBySpecList")
    public List<Map>    findBySpecList(long id){
        return typeTemplateService.findBySpecList(id);
    }
}
