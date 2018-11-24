package com.itheima.pyg.service.brand;

import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.good.Brand;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 查询全部品牌
     * @return
     */
    List<Brand> findAllBrands();

    /**
     * 分页查询品牌
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findBrandListByPage(Integer pageNum,Integer pageSize);

    /**
     * 根据条件分页查询
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    PageResult  searchBrands(Integer pageNum,Integer pageSize,Brand brand);

    /**
     * 添加品牌
     * @param brand
     * @return
     */
    Result  addBrand(Brand brand);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    Brand findOne(long id);

    /**
     * 更新品牌
     * @param brand
     */
    void updateBrand(Brand brand);

    /**
     * 删除品牌
     * @param ids
     */
    void deleteBrandsByIds(long[] ids);

    /**
     * 查询品牌列表项,用作模板关联品牌新增时下拉框数据显示
     * @return
     */
    List<Map<String,String>> selectOptionList();
}
