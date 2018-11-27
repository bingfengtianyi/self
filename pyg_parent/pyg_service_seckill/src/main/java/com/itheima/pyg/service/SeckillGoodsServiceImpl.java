package com.itheima.pyg.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.pyg.dao.seckill.SeckillGoodsDao;
import com.itheima.pyg.pojo.seckill.SeckillGoods;
import com.itheima.pyg.pojo.seckill.SeckillGoodsQuery;
import com.itheima.pyg.service.seckill.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询所有参与秒杀活动的商品列表
     * @return
     */
    @Override
    public List<SeckillGoods> findSeckillGoodList() {
        //先从缓存中尝试获取秒杀商品列表
        List<SeckillGoods> seckillGoodList = redisTemplate.boundHashOps("seckillGoodList").values();
        //如果缓存中没有商品,从数据库中查询并存入缓存
        if (seckillGoodList==null || seckillGoodList.size()==0){
            //从数据库中查询参加秒杀的商品
            SeckillGoodsQuery query = new SeckillGoodsQuery();
            //创建查询条件
            SeckillGoodsQuery.Criteria criteria = query.createCriteria();
            //已审核且库存大于0
            criteria.andStatusEqualTo("1").andNumGreaterThan(0);
            //秒杀开始时间小于结束时间
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            //秒杀结束时间大于现在时间
            criteria.andEndTimeGreaterThan(new Date());

            seckillGoodList = seckillGoodsDao.selectByExample(query);
            if (seckillGoodList!=null&&seckillGoodList.size()>0){
                for (SeckillGoods seckillGoods : seckillGoodList) {
                    redisTemplate.boundHashOps("seckillGoodList").put(seckillGoods.getId(),seckillGoods);
                }
            }
        }else {
            System.out.println("从缓存中查询");
        }
        return seckillGoodList;
    }

    /**
     * 根据id查询秒杀商品实体对象
     * @param id
     * @return
     */
    @Override
    public SeckillGoods findOneFromRedis(long id) {
        return (SeckillGoods) redisTemplate.boundHashOps("seckillGoodList").get(id);
    }

    /**
     * 查询所有参与秒杀活动的商品列表
     * @return
     */
    @Override
    public List<SeckillGoods> findSeckillGoodListFromDB() {
        //先从缓存中尝试获取秒杀商品列表
        List<SeckillGoods> seckillGoodList = redisTemplate.boundHashOps("seckillGoodList").values();
        //如果缓存中没有商品,从数据库中查询并存入缓存
        if (seckillGoodList==null || seckillGoodList.size()==0){
            //从数据库中查询参加秒杀的商品
            SeckillGoodsQuery query = new SeckillGoodsQuery();
            //创建查询条件
            SeckillGoodsQuery.Criteria criteria = query.createCriteria();
            //已审核且库存大于0
            criteria.andStatusEqualTo("1").andNumGreaterThan(0);
            //秒杀开始时间小于结束时间
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            //秒杀结束时间大于现在时间
            criteria.andEndTimeGreaterThan(new Date());

            seckillGoodList = seckillGoodsDao.selectByExample(query);
            if (seckillGoodList!=null&&seckillGoodList.size()>0){
                for (SeckillGoods seckillGoods : seckillGoodList) {
                    redisTemplate.boundHashOps("seckillGoodList").put(seckillGoods.getId(),seckillGoods);
                }
            }
        }else {
            System.out.println("从缓存中查询");
        }
        return seckillGoodList;
    }
}
