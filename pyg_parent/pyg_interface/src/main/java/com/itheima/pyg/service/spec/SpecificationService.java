package com.itheima.pyg.service.spec;

import com.github.pagehelper.Page;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.entity.vo.SpecificationVo;
import com.itheima.pyg.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    /**
     * 查询全部规格信息
     * @return
     */
    List<Specification> findAll();

    /**
     * 分页查询规格信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findPage(Integer pageNum, Integer pageSize);

    /**
     * 带条件的分页查询
     * @param pageNum
     * @param pageSize
     * @param specification
     * @return
     */
    PageResult search(Integer pageNum, Integer pageSize, Specification specification);

    /**
     * 新增规格
     * @param specificationVo
     */
    Result add(SpecificationVo specificationVo);

    /**
     * 查询实体对象
     * @param id
     * @return
     */
    SpecificationVo findOne(long id);

    /**
     * 更新规格
     * @param specificationVo
     * @return
     */
    Result update(SpecificationVo specificationVo);

    /**
     * 删除规格
     * @param ids
     * @return
     */
    Result delete(long[] ids);

    /**
     * 查询规格信息用于模板信息关联
     * @return
     */
    List<Map<String,String>> selectOptionList();

    /**
     * 运营商后台导入规格列表
     * @param specification
     */
    void save(Specification specification);
}
