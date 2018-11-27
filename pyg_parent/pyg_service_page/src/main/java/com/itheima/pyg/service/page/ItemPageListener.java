package com.itheima.pyg.service.page;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class ItemPageListener implements MessageListener {

    @Resource
    private ItemPageService itemPageService;


    @Override
    public void onMessage(Message message) {
        // 接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long goodsId = (Long) objectMessage.getObject();
            // 执行生成商品详细页
            itemPageService.genHtml(goodsId);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


}
