package com.itheima.pyg.controller.brand;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.good.Brand;
import com.itheima.pyg.service.brand.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询全部品牌
     * @return
     */
    @RequestMapping("findAllBrands")
    public List<Brand>  findAllBrands(){
        return brandService.findAllBrands();
    }

    /**
     * 分页查询品牌
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("findBrandListByPage")
    public PageResult findBrandListByPage(Integer pageNum,Integer pageSize){
        return brandService.findBrandListByPage(pageNum,pageSize);
    }

    /**
     * 根据条件分页查询品牌
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @RequestMapping("searchBrands")
    public PageResult   searchBrands(Integer pageNum,Integer pageSize,@RequestBody Brand brand){
        return brandService.searchBrands(pageNum,pageSize,brand);
    }

    /**
     * 新建品牌
     * @param brand
     * @return
     */
    @RequestMapping("addBrand")
    public Result addBrand(@RequestBody Brand brand){
        return brandService.addBrand(brand);
    }

    /**
     * 更新数据前,根据id查询品牌信息用于回显
     * @param id
     * @return
     */
    @RequestMapping("findOne")
    public Brand    findOne(long id){
        return brandService.findOne(id);
    }

    /**
     * 更新品牌数据
     * @param brand
     * @return
     */
    @RequestMapping("updateBrand")
    public Result   updateBrand(@RequestBody Brand brand){
        Result result =  null;
        try {
            brandService.updateBrand(brand);
            result = new Result(true,"更新品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"更新品牌失败");
        }finally {
            return result;
        }
    }

    /**
     * 根据ids删除品牌
     * @param ids
     * @return
     */
    @RequestMapping("deleteBrandsByIds")
    public Result   deleteBrandsByIds(long[] ids){
        Result result = null;
        try {
            brandService.deleteBrandsByIds(ids);
            result = new Result(true,"删除品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"删除品牌失败");
        } finally {
            return result;
        }
    }

    /**
     * 查询品牌列表项,用作模板关联品牌新增时下拉框数据显示
     * @return
     */
    @RequestMapping("selectOptionList")
    public List<Map<String,String>>  selectOptionList(){
        return brandService.selectOptionList();
    }

}
