package com.itheima.pyg.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.Result;
import com.itheima.pyg.pojo.good.Goods;
import com.itheima.pyg.service.goods.GoodsService;
import com.itheima.pyg.service.page.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemPageService itemPageService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination topicPageDestination;

    @Autowired
    private Destination topicPageDeleteDestination;

    @RequestMapping("genHtml")
    public void genHtml(long goodsId){
        itemPageService.genHtml(goodsId);
    }

    /**
     * 运营商查询全部待审核商品
     * @param pageNum
     * @param pageSize
     * @param goods
     * @return
     */
    @RequestMapping("searchForManager")
    public PageResult searchForManager(Integer pageNum, Integer pageSize, @RequestBody Goods goods){
        return goodsService.searchForManager(pageNum,pageSize,goods);
    }

    /**
     * 商品审核,即修改状态
     * 审核成功的要添加到索引库,生成静态页面
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("updateStatus")
    public Result   updateStatus(long[] ids,String status){
        try {
            goodsService.updateStatus(ids,status);

            /*审核成功,生成静态详情页*/
            for (final Long goodsId : ids) {

                /*审核成功发送消息队列*/
                jmsTemplate.send(topicPageDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(goodsId);
                    }
                });
            }
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");
        }
    }

    /**
     * 逻辑删除待审核商品
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    public Result delete(final Long[] ids){
        try {
            goodsService.delete(ids);
            /*删除商品,删除静态详情页*/
                /*删除后发送消息队列*/
                jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        System.out.println("发送消息队列");
                        return session.createObjectMessage(ids);
                    }
                });

            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
