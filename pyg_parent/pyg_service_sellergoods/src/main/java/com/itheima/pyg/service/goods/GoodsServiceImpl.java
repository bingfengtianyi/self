package com.itheima.pyg.service.goods;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.pyg.dao.good.BrandDao;
import com.itheima.pyg.dao.good.GoodsDao;
import com.itheima.pyg.dao.good.GoodsDescDao;
import com.itheima.pyg.dao.item.ItemCatDao;
import com.itheima.pyg.dao.item.ItemDao;
import com.itheima.pyg.dao.seller.SellerDao;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.vo.GoodsVo;
import com.itheima.pyg.pojo.good.Goods;
import com.itheima.pyg.pojo.good.GoodsDesc;
import com.itheima.pyg.pojo.good.GoodsQuery;
import com.itheima.pyg.pojo.item.Item;
import com.itheima.pyg.pojo.item.ItemQuery;
import com.itheima.pyg.pojo.order.Order;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private BrandDao brandDao;

    @Resource
    private SellerDao sellerDao;

    @Resource
    private SolrTemplate solrTemplate;

    @Override
    public List<Goods> getGoodList() {
        return goodsDao.selectByExample(null);
    }

    /**
     * 商品录入
     *
     * @param goodsVo
     * @return
     */
    @Override
    public void add(GoodsVo goodsVo) {
        //保存商品信息
        Goods goods = goodsVo.getGoods();
        goods.setAuditStatus("0");
        goodsDao.insertSelective(goods);
        //保存商品描述信息
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);
        //保存商品对应的库存信息
        //先判断商品是否启用规格
        if ("1".equals(goods.getIsEnableSpec())){
            List<Item> itemList = goodsVo.getItemList();
            if (itemList!=null&&itemList.size()>0){
                for (Item item : itemList) {
                    item.setGoodsId(goods.getId());
                    String title = goods.getGoodsName()+" "+goods.getCaption();
                    Map<String,String> map = JSON.parseObject(item.getSpec(), Map.class);
                    if (map!=null&&map.size()>0){
                        for (Map.Entry<String,String> entry : map.entrySet()) {
                            title += entry.getValue();
                        }
                    }
                    item.setTitle(title);
                    //[{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]
                    setAttributeForItem(goods,goodsDesc,item);
                    itemDao.insertSelective(item);
                }
            }
        }else {
            Item item = new Item();
            item.setTitle(goods.getGoodsName()+" "+goods.getCaption());
            item.setPrice(goods.getPrice());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setSpec("{}");
            setAttributeForItem(goods,goodsDesc,item);
            itemDao.insertSelective(item);
        }


    }

    private void setAttributeForItem(Goods goods, GoodsDesc goodsDesc, Item item) {
        List<Map> maps = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
        if (maps!=null&&maps.size()>0){
            //默认只要第一张图片
            String url = maps.get(0).get("url").toString();
            item.setImage(url);
        }
        item.setCategoryid(goods.getCategory3Id());
        item.setStatus("1");
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setGoodsId(goods.getId());
        item.setSellerId(goods.getSellerId());
        item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName());
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getNickName());
    }

    /**
     * 分页查询商品列表
     * @param pageNum
     * @param pageSize
     * @param goods
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Goods goods) {
        PageHelper.startPage(pageNum,pageSize);
        GoodsQuery goodsQuery = new GoodsQuery();
        if (goods.getSellerId()!=null&&!"".equals(goods.getSellerId().trim())){
            goodsQuery.createCriteria().andSellerIdEqualTo(goods.getSellerId());
        }
        if (goods.getAuditStatus()!=null&&!"".equals(goods.getAuditStatus())){
            goodsQuery.createCriteria().andAuditStatusEqualTo(goods.getAuditStatus());
        }
        if (goods.getGoodsName()!=null&&!"".equals(goods.getGoodsName().trim())){
            goodsQuery.createCriteria().andGoodsNameLike("%"+goods.getGoodsName().trim()+"%");
        }
        goodsQuery.setOrderByClause("id desc");
        Page<Goods> page = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 商品数据回显
     * @param id
     * @return
     */
    @Override
    public GoodsVo findOne(long id) {
        Goods goods = goodsDao.selectByPrimaryKey(id);
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        GoodsVo goodsVo = new GoodsVo();
        goodsVo.setGoods(goods);
        goodsVo.setGoodsDesc(goodsDesc);
        goodsVo.setItemList(itemList);
        return goodsVo;
    }

    /**
     * 更新商品数据
     * @param goodsVo
     */
    @Override
    public void update(GoodsVo goodsVo) {
        Goods goods = goodsVo.getGoods();
        goods.setAuditStatus("0");
        goodsDao.updateByPrimaryKeySelective(goods);
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);
        //更新库存信息,先删除再插入
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(itemQuery);

        if ("1".equals(goods.getIsEnableSpec())){
            List<Item>  itemList = goodsVo.getItemList();
            for (Item item : itemList) {
                String title = goods.getGoodsName()+" "+goods.getCaption();
                Map<String,String> map = JSON.parseObject(item.getSpec(), Map.class);
                if (map!=null&&map.size()>0){
                    for (Map.Entry<String,String> entry : map.entrySet()) {
                        title += entry.getValue();
                    }
                }
                item.setTitle(title);
                setAttributeForItem(goods, goodsDesc,item);
                itemDao.insertSelective(item);
            }
        }else {
            Item item = new Item();
            item.setTitle(goods.getGoodsName()+" "+goods.getCaption());
            item.setPrice(goods.getPrice());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setSpec("{}");
            setAttributeForItem(goods,goodsDesc,item);
            itemDao.insertSelective(item);
        }
    }

    /**
     * 运营商查询全部待审核商品
     * @param pageNum
     * @param pageSize
     * @param goods
     * @return
     */
    @Override
    public PageResult searchForManager(Integer pageNum, Integer pageSize, Goods goods) {
        PageHelper.startPage(pageNum,pageSize);
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        if (goods.getAuditStatus()!=null&&!"".equals(goods.getAuditStatus())){
            criteria.andAuditStatusEqualTo(goods.getAuditStatus().trim());
        }
        criteria.andIsDeleteIsNull();
        goodsQuery.setOrderByClause("id desc");
        Page<Goods> page = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 运营商进行商品审核,即修改商品状态
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(long[] ids, String status) {
        Goods goods = new Goods();
        goods.setAuditStatus(status);
        if (ids!=null&&ids.length>0){
            for (long id : ids) {
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
              /*  if ("1".equals(status)){//审核成功
                    //dataImportItemToSolr();
                    //TODO  生成静态页面
                }*/
            }
        }
    }

    /**
     * 将数据库中的数据保存到索引库
     */
   /* private void dataImportItemToSolr(){
        List<Item> itemList = itemDao.selectByExample(null);
        if (itemList!=null&&itemList.size()>0){
            for (Item item : itemList) {
                String spec = item.getSpec();
                Map map = JSON.parseObject(spec, Map.class);
                item.setSpecMap(map);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }

    }*/

    /**
     * 逻辑删除待审核商品
     * 其实就是更改是否删除状态,之后更新
     * @param ids
     */
    @Override
    public void delete(long[] ids) {
        Goods goods = new Goods();
        goods.setIsDelete("1");
        if (ids!=null&&ids.length>0){
            for (long id : ids) {
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                //TODO 更新索引库
                //TODO 删除商品静态页面(可选)
            }
        }
    }

    @Override
    public PageResult<Goods> getGoodsListByPage(Integer pageNum, Integer pageSize) {
        //设置分页查询条件
        PageHelper.startPage(pageNum,pageSize);
        //进行查询
        Page<Goods> page = (Page<Goods>) goodsDao.selectByExample(null);
        //封装PageResult对象
        PageResult<Goods>   pageResult = new PageResult<>(page.getTotal(),page.getResult());
        return pageResult;
    }

    /*根据商品id查询库存列表*/
    @Override
    public List<Item> findItemList(long[] ids) {

        List<Item> itemList = new ArrayList<>();
        for (Long id : ids) {
            ItemQuery query = new ItemQuery();
            query.createCriteria().andGoodsIdEqualTo(id);
            List<Item> Items = itemDao.selectByExample(query);
            itemList.addAll(Items);
        }
        return itemList;
    }
    /*根据商品id查询库存得到库存id*/
    @Override
    public List<String> findItemIds(long[] ids) {
        List<String> itemIds = new ArrayList<>();
        for (Long id : ids) {
            ItemQuery query = new ItemQuery();
            query.createCriteria().andGoodsIdEqualTo(id);
            List<Item> Items = itemDao.selectByExample(query);
            for (Item Item : Items) {
                itemIds.add(Item.getId()+"");
            }
        }
        return itemIds;
    }
}
