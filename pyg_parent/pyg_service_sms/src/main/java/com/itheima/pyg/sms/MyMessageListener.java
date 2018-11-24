package com.itheima.pyg.sms;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.*;

public class MyMessageListener implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;

    @Value("${templateCode_smscode}")
    private String templateCode;

    @Value("${templateParam_smscode}")
    private String param;

    public void onMessage(Message message) {
        try {
            MapMessage mapMessage = (MapMessage) message;
            String phone = mapMessage.getString("phone");
            String smscode = mapMessage.getString("smscode");
            System.out.println("接收到消息:"+phone+" "+smscode);

            smsUtil.sendSms(phone,templateCode,param.replace("[value]",smscode));

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
