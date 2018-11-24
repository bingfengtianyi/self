package com.itheima.pyg.controller.spec;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.entity.vo.SpecificationVo;
import com.itheima.pyg.pojo.specification.Specification;
import com.itheima.pyg.service.spec.SpecificationService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    /**
     * 查询全部规格信息
     * @return
     */
    @RequestMapping("findAll")
    public List<Specification>  findAll(){
        return specificationService.findAll();
    }

    /**
     * 分页查询规格信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("findPage")
    public PageResult findPage(Integer pageNum,Integer pageSize){
        return specificationService.findPage(pageNum,pageSize);
    }

    /**
     * 带条件的分页查询
     * @param pageNum
     * @param pageSize
     * @param specification
     * @return
     */
    @RequestMapping("search")
    public PageResult   search(Integer pageNum,Integer pageSize,@RequestBody Specification specification){
        return specificationService.search(pageNum,pageSize,specification);
    }

    /**
     * 新增规格
     * @param specificationVo
     */
    @RequestMapping("add")
    public Result add(@RequestBody SpecificationVo specificationVo){
        return specificationService.add(specificationVo);
    }

    /**
     * 查询规格及规格项的包装类对象,用于回显数据
     * @param id
     * @return
     */
    @RequestMapping("findOne")
    public SpecificationVo findOne(long id){
        return specificationService.findOne(id);
    }

    /**
     * 更新规格
     * @param specificationVo
     * @return
     */
    @RequestMapping("update")
    public Result   update(@RequestBody SpecificationVo specificationVo){
        return specificationService.update(specificationVo);
    }

    /**
     * 删除规格
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    public Result delete(long[] ids){
        return specificationService.delete(ids);
    }

    /**
     * 查询规格信息用于模板信息关联
     * @return
     */
    @RequestMapping("selectOptionList")
    public List<Map<String,String>> selectOptionList(){
        return specificationService.selectOptionList();
    }
}
