package com.itheima.pyg.service.search;

import com.alibaba.fastjson.JSON;
import com.itheima.pyg.pojo.item.Item;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchLinstener implements MessageListener {


    @Resource
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {

        try {
            // 接收消息
            TextMessage textMessage = (TextMessage) message;
            String json = textMessage.getText();
            // 将json转换为itemList对象
            List<Item> itemList = JSON.parseArray(json, Item.class);

            if (itemList!=null&&itemList.size()>0){
                for (Item item : itemList) {
                    String spec = item.getSpec();
                    Map map = JSON.parseObject(spec, Map.class);
                    item.setSpecMap(map);
                }
                solrTemplate.saveBeans(itemList);
                solrTemplate.commit();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
