package com.itheima.pyg.service.template;

import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    /**
     * 带条件的额分页查询
     * @param pageNum
     * @param pageSize
     * @param typeTemplate
     * @return
     */
    PageResult search(Integer pageNum, Integer pageSize, TypeTemplate typeTemplate);

    /**
     * 添加模板
     * @param typeTemplate
     * @return
     */
    Result add(TypeTemplate typeTemplate);

    /**
     * 更新模板
     * @param typeTemplate
     * @return
     */
    Result update(TypeTemplate typeTemplate);

    /**
     * 查询模板实体对象
     * @param id
     * @return
     */
    TypeTemplate findOne(long id);

    /**
     * 删除模板
     * @param ids
     * @return
     */
    Result delete(long[] ids);

    /**
     * 根据模板id查找规格和规格选项
     * @param id
     * @return
     */
    List<Map> findBySpecList(long id);
}
