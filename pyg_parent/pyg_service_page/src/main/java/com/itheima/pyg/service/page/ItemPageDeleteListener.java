package com.itheima.pyg.service.page;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class ItemPageDeleteListener implements MessageListener {

    @Resource
    private ItemPageService itemPageService;


    @Override
    public void onMessage(Message message) {

        System.out.println("监听");

            // 接收消息
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                Long[] ids = (Long[]) objectMessage.getObject();
                System.out.println("监听"+ids);
                // 执行删除商品详细页
                itemPageService.deleteItemPage(ids);
            } catch (JMSException e) {
                e.printStackTrace();
            }

    }
}
