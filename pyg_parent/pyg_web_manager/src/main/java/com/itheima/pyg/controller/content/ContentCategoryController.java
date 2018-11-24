package com.itheima.pyg.controller.content;

import java.util.List;

import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.ad.ContentCategory;
import com.itheima.pyg.service.content.ContentCategoryService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

	@Reference
	private ContentCategoryService categoryService;

	@RequestMapping("findAll")
	public List<ContentCategory> list() throws Exception {
		List<ContentCategory> list = categoryService.findAll();
		return list;
	}

	@RequestMapping("findPage")
	public PageResult findPage(Integer pageNum, Integer pageSize) throws Exception {
		PageResult pageResult = categoryService.findPage(null, pageNum, pageSize);
		return pageResult;
	}

	@RequestMapping("findOne")
	public ContentCategory findOne(Long id) throws Exception {
		ContentCategory contentCategory = categoryService.findOne(id);
		return contentCategory;
	}

	@RequestMapping("update")
	public Result update(@RequestBody ContentCategory contentCategory) throws Exception {
		try {
			categoryService.edit(contentCategory);
			return new Result(true, "修改成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败!");
		}
	}

	@RequestMapping("add")
	public Result add(@RequestBody ContentCategory contentCategory) throws Exception {
		try {
			categoryService.add(contentCategory);
			return new Result(true, "保存成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败!");
		}
	}

	@RequestMapping("delete")
	public Result delete(Long[] ids) throws Exception {
		try {
			categoryService.delAll(ids);
			return new Result(true, "删除成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败!");
		}
	}

	@RequestMapping("search")
	public PageResult search(@RequestBody ContentCategory contentCategory, Integer pageNum, Integer pageSize)
			throws Exception {
		PageResult pageResult = categoryService.findPage(contentCategory, pageNum, pageSize);
		return pageResult;
	}
}
