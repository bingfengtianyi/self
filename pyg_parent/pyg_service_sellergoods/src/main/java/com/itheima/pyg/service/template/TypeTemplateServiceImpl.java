package com.itheima.pyg.service.template;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.pyg.dao.specification.SpecificationOptionDao;
import com.itheima.pyg.dao.template.TypeTemplateDao;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.specification.SpecificationOption;
import com.itheima.pyg.pojo.specification.SpecificationOptionQuery;
import com.itheima.pyg.pojo.template.TypeTemplate;
import com.itheima.pyg.pojo.template.TypeTemplateQuery;
import com.itheima.pyg.service.template.TypeTemplateService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class TypeTemplateServiceImpl    implements TypeTemplateService {

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 带条件的分页查询
     * @param pageNum
     * @param pageSize
     * @param typeTemplate
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, TypeTemplate typeTemplate) {
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        if (typeTemplates!=null&&typeTemplates.size()>0){
            for (TypeTemplate template : typeTemplates) {
                //品牌结果集
                String brandIds = template.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
                //规格选项结果集
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(),specList);
            }
        }
        //设置分页条件
        PageHelper.startPage(pageNum,pageSize);
        String name = typeTemplate.getName();
        //封装查询条件
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        if (name!=null&&!"".equalsIgnoreCase(name.trim())){
           typeTemplateQuery.createCriteria().andNameLike("%"+name.trim()+"%");
        }
        typeTemplateQuery.setOrderByClause("id desc");
        Page<TypeTemplate> page = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 添加模板
     * @param typeTemplate
     * @return
     */
    @Override
    public Result add(TypeTemplate typeTemplate) {
        Result result = null;
        try {
            typeTemplateDao.insertSelective(typeTemplate);
            result = new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"添加失败");
        } finally {
            return result;
        }
    }

    /**
     * 更新模板
     * @param typeTemplate
     * @return
     */
    @Override
    public Result update(TypeTemplate typeTemplate) {
        Result result = null;
        try {
            typeTemplateDao.updateByPrimaryKey(typeTemplate);
            result = new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"更新失败");
        } finally {
            return result;
        }
    }

    /**
     * 查询模板实体对象
     * @param id
     * @return
     */
    @Override
    public TypeTemplate findOne(long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }


    /**
     * 删除模板
     * @param ids
     * @return
     */
    @Override
    public Result delete(long[] ids) {
        Result result = null;
        try {
            TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
            List<Long>  idsList = new ArrayList<>();
            if (ids!=null&&ids.length>0){
                for (long id : ids) {
                    idsList.add(id);
                }
            }
            typeTemplateQuery.createCriteria().andIdIn(idsList);
            typeTemplateDao.deleteByExample(typeTemplateQuery);
            result = new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"删除失败");
        } finally {
            return result;
        }
    }

    /**
     * 根据模板id查找规格和规格选项
     * 封装成map的list集合,map中包含规格id,规格名以及对应的规格选项列表
     * @param id
     * @return
     */
    @Override
    public List<Map> findBySpecList(long id) {
        //根据模板id查到模板对象
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //根据模板对象获得对应的规格对象,为一个json字符串
        String specIds = typeTemplate.getSpecIds();
        //用fastjosn将json字符串转成json对象
        //[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        //根据规格查询规格选项
        if (maps!=null&&maps.size()>0){
            for (Map map : maps) {
                long specId = Long.parseLong(map.get("id").toString());
                SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
                specificationOptionQuery.createCriteria().andSpecIdEqualTo(specId);
                List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);
                map.put("options",specificationOptions);
            }
        }
        return maps;
    }
}
