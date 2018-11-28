package com.itheima.pyg.service.search;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.itheima.pyg.pojo.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class ItemSearchServiceImpl  implements ItemSearchService {

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 前台系统检索
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        Map<String,Object>  resultMap = new HashMap<>();
        //处理输入的关键字的内容中包含的空格
        String keywords = searchMap.get("keywords");
        if(keywords != null && !"".equals(keywords)){
            keywords = keywords.replace(" ", "");
            searchMap.put("keywords", keywords);
        }
        //根据关键字检索,并且分页
//        Map<String,Object>  map = searchForPage(searchMap);
        //根据关键字检索、分页并且高亮
        Map<String,Object>  map = searchForHighLightPage(searchMap);
        resultMap.putAll(map);
        //根据关键字查询商品的分类：分组
        List<String>    categoryList = queryForGroupPage(searchMap);
        //默认加载第一个分类下的品牌以及规格
        if (categoryList!=null&&categoryList.size()>0){
            Map<String,Object> brandAndSpecMap = searchBrandListAndSpecLisstForCategory1(categoryList.get(0));
            resultMap.putAll(brandAndSpecMap);
            resultMap.put("categoryList",categoryList);
        }
        return resultMap;
    }

    private Map<String, Object> searchBrandListAndSpecLisstForCategory1(String name) {
        Object typeId = redisTemplate.boundHashOps("itemCat").get(name);
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        List<Map>   specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        Map<String,Object> map = new HashMap<>();
        map.put("brandList",brandList);
        map.put("specList",specList);
        return map;
    }

    /**
     * 根据关键字查询商品的分类：分组
     * @param searchMap
     * @return
     */
    private List<String> queryForGroupPage(Map<String, String> searchMap) {
        //封装检索的条件
        String keywords = searchMap.get("keywords");
        Criteria criteria = new Criteria("item_keywords");
        if (keywords!=null&&!"".equals(keywords)){
            criteria.is(keywords);
        }
        Query query = new SimpleQuery(criteria);
        //设置分组条件
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //根据条件进行查询
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);
        //处理结果集
        List<String> categoryList = new ArrayList<>();
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            categoryList.add(groupValue);
        }
        return categoryList;
    }

    /**
     * 根据关键字检索、分页并且关键字高亮的方法
     * @param searchMap
     * @return
     */
    private Map<String, Object> searchForHighLightPage(Map<String, String> searchMap) {
        //设置检索条件
        String keywords = searchMap.get("keywords");
        //设置在solr索引库中按什么字段检索
        Criteria criteria = new Criteria("item_keywords");
        if (keywords!=null&&!"".equals(keywords)){
            //封装检索条件
            criteria.is(keywords);//模糊查询
        }
        HighlightQuery query = new SimpleHighlightQuery(criteria);
        //设置分页条件
        Integer pageNo = Integer.parseInt(searchMap.get("pageNo"));
        Integer pageSize = Integer.parseInt(searchMap.get("pageSize"));
        Integer offset = (pageNo-1)*pageSize;
        query.setOffset(offset);
        query.setRows(pageSize);
        //设置关键字高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<font color='red'>");
        highlightOptions.setSimplePostfix("</font>");
        query.setHighlightOptions(highlightOptions);
        //添加过滤条件
        //商品分类
        String category = searchMap.get("category");
        if (category!=null&&!"".equals(category)){
            Criteria cri = new Criteria("item_category");
            cri.is(category);
            FilterQuery filterQuery = new SimpleFilterQuery(cri);
            query.addFilterQuery(filterQuery);
        }
        //商品品牌
        String brand = searchMap.get("brand");
        if (brand!=null&&!"".equals(brand)){
            Criteria cri = new Criteria("item_brand");
            cri.is(brand);
            FilterQuery filterQuery = new SimpleFilterQuery(cri);
            query.addFilterQuery(filterQuery);
        }
        //商品规格
        String spec = searchMap.get("spec");
        if (spec!=null&&!"".equals(spec)){
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Criteria cri = new Criteria("item_spec_"+entry.getKey());
                cri.is(entry.getValue());
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
                query.addFilterQuery(filterQuery);
            }
        }
        //商品价格
        String price = searchMap.get("price");
        if (price!=null&&!"".equals(price)){
            String[] split = price.split("-");
            Criteria cri = new Criteria("item_price");
            cri.between(split[0],split[1],true,true);
            FilterQuery filterQuery = new SimpleFilterQuery(cri);
            query.addFilterQuery(filterQuery);
        }
        //结果排序：新品、价格
        // 根据新品排序：sortField，排序字段   sort：排序规则
        String s = searchMap.get("sort");
        if(s != null && !"".equals(s)){
            if("ASC".equals(s)){
                Sort sort = new Sort(Sort.Direction.ASC, "item_"+searchMap.get("sortField"));
                query.addSort(sort);
            }else{
                Sort sort = new Sort(Sort.Direction.DESC, "item_"+searchMap.get("sortField"));
                query.addSort(sort);
            }
        }
        //根据条件查询
        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);
        //处理高亮的结果
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();
        if (highlighted!=null&&highlighted.size()>0){
            for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
                Item entity = itemHighlightEntry.getEntity();
                List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
                if (highlights!=null&&highlights.size()>0){
                    for (HighlightEntry.Highlight highlight : highlights) {
                        String title = highlight.getSnipplets().get(0);
                        entity.setTitle(title);
                    }
                }
            }
        }
        //处理结果集
        Map<String,Object>  map = new HashMap<>();
        map.put("totalPages",highlightPage.getTotalPages());
        map.put("total",highlightPage.getTotalElements());
        map.put("rows",highlightPage.getContent());
        return map;
    }

    /**
     * 根据关键字检索并分页的方法
     * @param searchMap
     * @return
     */
    private Map<String, Object> searchForPage(Map<String, String> searchMap) {
        //设置查询的关键字
        String keywords = searchMap.get("keywords");
        Criteria criteria = new Criteria("item_keywords");
        if (keywords!=null&&!"".equals(keywords)){
            criteria.is(keywords);
        }
        Query query = new SimpleQuery(criteria);
        //设置分页
        Integer pageNo = Integer.parseInt(searchMap.get("pageNo"));
        Integer pageSize = Integer.parseInt(searchMap.get("pageSize"));
        Integer offset = (pageNo-1)*pageSize;
        query.setOffset(offset);
        query.setRows(pageSize);
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);
        //处理结果集,封装到map中
        Map<String,Object>  map = new HashMap<>();
        map.put("totalPages",scoredPage.getTotalPages());
        map.put("total",scoredPage.getTotalElements());
        map.put("rows",scoredPage.getContent());
        return map;
    }




}
