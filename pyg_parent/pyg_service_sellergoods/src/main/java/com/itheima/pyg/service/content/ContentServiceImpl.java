package com.itheima.pyg.service.content;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.itheima.pyg.dao.ad.ContentDao;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.pojo.ad.Content;
import com.itheima.pyg.pojo.ad.ContentQuery;
import com.itheima.pyg.service.content.ContentService;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ContentServiceImpl implements ContentService {

	@Autowired
	private ContentDao contentDao;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 运营商对广告增删改后,更新缓存,即清除缓存
	 * @param categoryId
	 */
	private void clearCache(long categoryId){
		redisTemplate.boundHashOps("content").delete(categoryId);
	}

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(Content content) {
		clearCache(content.getCategoryId());
		contentDao.insertSelective(content);
	}

	@Override
	public void edit(Content content) {
		Long newCategoryId = content.getCategoryId();
		Long OldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
		if (newCategoryId!=OldCategoryId){
			clearCache(newCategoryId);
		}
		clearCache(OldCategoryId);
		contentDao.updateByPrimaryKeySelective(content);
	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				Long categoryId = contentDao.selectByPrimaryKey(id).getCategoryId();
				clearCache(categoryId);
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}

	/**
	 * 根据广告分类id查询广告列表
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<Content> findByCategoryId(long categoryId) {
		List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
		if (contentList==null){
			synchronized (this){
				contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
				if (contentList==null){
					ContentQuery contentQuery = new ContentQuery();
					contentQuery.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
					contentList = contentDao.selectByExample(contentQuery);
					redisTemplate.boundHashOps("content").put(categoryId,contentList);
					redisTemplate.boundHashOps("content").expire(1,TimeUnit.DAYS);
				}
			}
		}
		return contentList;
	}
}
