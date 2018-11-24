package com.itheima.pyg.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.pyg.dao.seckill.SeckillGoodsDao;
import com.itheima.pyg.dao.seckill.SeckillOrderDao;
import com.itheima.pyg.pojo.seckill.SeckillGoods;
import com.itheima.pyg.pojo.seckill.SeckillOrder;
import com.itheima.pyg.service.seckill.SeckillOrderService;
import com.itheima.pyg.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class SeckillOrderServiceImpl    implements SeckillOrderService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;
    /**
     * 提交秒杀订单
     * @param seckillId
     * @param userId
     */
    @Override
    public void submitOrder(long seckillId, String userId) {
        //限定一个用户同一时间只能下一单
        if (redisTemplate.boundHashOps("seckillOrder").get(userId)!=null){
            throw new RuntimeException("请先完成上一笔订单");
        }
        //根据seckillId从redis中查询秒杀商品
        SeckillGoods seckillGood = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodList").get(seckillId);
        //如果库存为0或商品为空,则同步数据到数据库
        if (seckillGood==null||seckillGood.getNum()<=0){
            throw new RuntimeException("商品已被抢空");
        }
        //扣减库存
        seckillGood.setNum(seckillGood.getNum()-1);
        seckillGood.setStockCount(seckillGood.getStockCount()+1);
        redisTemplate.boundHashOps("seckillGoodList").put(seckillId,seckillGood);
        //扣减库存之后,如果数量为0,则同步数据到数据库,同时清空缓存
        if (seckillGood.getNum()==0){
            seckillGoodsDao.updateByPrimaryKeySelective(seckillGood);
            redisTemplate.boundHashOps("seckillGoodList").delete(seckillId);
        }
        //将秒杀预订单保存到redis
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(seckillId);
        seckillOrder.setMoney(seckillGood.getCostPrice());
        seckillOrder.setUserId(userId);
        seckillOrder.setSellerId(seckillGood.getSellerId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");
        redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder);
    }
    //TODO 如果下单失败,应该回滚redis和数据库中被修改的数据


    /**
     * 根据用户名从redis中提取订单
     * @param userId
     * @return
     */
    @Override
    public SeckillOrder searchOrderFromRedis(String userId) {
        return (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }

    /**
     * 支付成功后,将redis中的订单同步到数据库,并且清除redis中的订单
     * @param userId
     * @param orderId
     * @param transactionId
     */
    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        SeckillOrder seckillOrder = searchOrderFromRedis(userId);
        if (seckillOrder==null){
            throw new RuntimeException("订单不存在");
        }
        if (seckillOrder.getId().longValue()!=orderId.longValue()){
            throw new RuntimeException("订单号不相符");
        }
        seckillOrder.setStatus("1");
        seckillOrder.setPayTime(new Date());
        seckillOrder.setTransactionId(transactionId);

        seckillOrderDao.insertSelective(seckillOrder);

        redisTemplate.boundHashOps("seckillOrder").delete(userId);
    }

    /**
     * 订单超时后从缓存中清除订单,回复库存
     * @param userId
     * @param orderId
     */
    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        //从redis中获取用户订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        //如果redis中存在此订单且订单号匹配,回复库存
        if (seckillOrder!=null&&seckillOrder.getId().longValue()==orderId.longValue()){
            SeckillGoods seckillGood = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodList").get(seckillOrder.getSeckillId());
            //如果redis中存在此商品,说明还没有被秒杀光,直接在redis中修改库存
            if (seckillGood!=null){
                seckillGood.setNum(seckillGood.getNum()+1);
                seckillGood.setStockCount(seckillGood.getStockCount()-1);
                redisTemplate.boundHashOps("seckillGoodList").put(seckillOrder.getSeckillId(),seckillGood);
            }else {//此商品已经被秒杀光,直接从数据库中修改,且此商品不再参加秒杀
                //从数据库中查询秒杀商品
                SeckillGoods seckillGoods = seckillGoodsDao.selectByPrimaryKey(seckillOrder.getSeckillId().longValue());
                if (seckillGoods!=null){
                    seckillGoods.setNum(seckillGoods.getNum()+1);
                    seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
                    seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);
                }else {
                    throw new RuntimeException("缓存和数据库中都没有此商品了!");
                }
            }
        }else {
            throw new RuntimeException("无此订单或订单号不匹配");
        }
        //清除缓存中的订单
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
    }
}
