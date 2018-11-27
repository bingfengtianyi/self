package com.itheima.pyg.service.page;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.pyg.dao.good.GoodsDao;
import com.itheima.pyg.dao.good.GoodsDescDao;
import com.itheima.pyg.dao.item.ItemCatDao;
import com.itheima.pyg.dao.item.ItemDao;
import com.itheima.pyg.pojo.good.Goods;
import com.itheima.pyg.pojo.good.GoodsDesc;
import com.itheima.pyg.pojo.item.Item;
import com.itheima.pyg.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Value("${pageDir}")
    private String pageDir;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemCatDao  itemCatDao;

    @Autowired
    private ItemDao itemDao;

    @Override
    public boolean genHtml(long goodsId) {
        //创建配置类,模板文件位置及编码已经在配置文件中指定
        Configuration configuration = freeMarkerConfig.getConfiguration();
        //加载指定目录中的模板文件
        Template template = null;
        try {
            template = configuration.getTemplate("item.ftl");
            //创建数据模型,用map封装
            Map dataModel = new HashMap();
            //商品信息
            Goods goods = goodsDao.selectByPrimaryKey(goodsId);
            dataModel.put("goods",goods);
            //商品描述
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc",goodsDesc);
            //商品分类面包屑
            String category1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String category2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String category3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName();
            dataModel.put("category1",category1);
            dataModel.put("category2",category2);
            dataModel.put("category3",category3);
            //sku列表
            ItemQuery itemQuery = new ItemQuery();
            ItemQuery.Criteria criteria = itemQuery.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            itemQuery.setOrderByClause("is_default desc");
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            dataModel.put("itemList",itemList);
            //创建输出流
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(pageDir +goodsId+".html"),"UTF-8");
            //输出生成商品详情静态页面
            template.process(dataModel, out);
            //关闭资源
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteItemPage(Long[] goodsIds) {
        System.out.println("删除详情页");
        try {
            for (Long goodsId : goodsIds) {
                // 删除文件夹中的文件
                new File(pageDir + goodsId + ".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
