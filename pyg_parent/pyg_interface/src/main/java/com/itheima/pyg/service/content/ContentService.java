package com.itheima.pyg.service.content;

import java.util.List;

import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.pojo.ad.Content;

public interface ContentService {

	public List<Content> findAll();

	public PageResult findPage(Content content, Integer pageNum, Integer pageSize);

	public void add(Content content);

	public void edit(Content content);

	public Content findOne(Long id);

	public void delAll(Long[] ids);

    List<Content> findByCategoryId(long categoryId);
}
