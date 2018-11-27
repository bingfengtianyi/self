package com.itheima.pyg.service.order;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.pyg.dao.item.ItemDao;
import com.itheima.pyg.dao.log.PayLogDao;
import com.itheima.pyg.dao.order.OrderDao;
import com.itheima.pyg.dao.order.OrderItemDao;
import com.itheima.pyg.entity.PageResult;
import com.itheima.pyg.entity.vo.Cart;
import com.itheima.pyg.pojo.item.Item;
import com.itheima.pyg.pojo.log.PayLog;
import com.itheima.pyg.pojo.order.Order;
import com.itheima.pyg.pojo.order.OrderItem;
import com.itheima.pyg.pojo.order.OrderQuery;
import com.itheima.pyg.pojo.user.User;
import com.itheima.pyg.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private PayLogDao payLogDao;

    @Autowired
    private ItemDao itemDao;

    /**
     * 分页获得订单列表
     * @return
     */
    @Override
    public PageResult<Order> getOrderListByPage(Integer pageNum, Integer pageSize) {
        //设置分页查询条件
        PageHelper.startPage(pageNum,pageSize);
        //进行查询
        Page<Order> page = (Page<Order>) orderDao.selectByExample(null);
        //封装PageResult对象
        PageResult<Order>   pageResult = new PageResult<>(page.getTotal(),page.getResult());
        return pageResult;
    }





    /**
     * 保存订单信息及订单明细到数据库
     *
     * @param order
     */
    @Override
    public void add(Order order) {
        //1.从redis中获取购物车数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        if (cartList == null) {
            throw new RuntimeException("购物车为空");
        }
        //2.保存订单和订单明细
        //支付订单号
        String outTradeNo = idWorker.nextId() + "";
        long totalMoney = 0L;
        Order tbOrder = new Order();
        for (Cart cart : cartList) {
            for (OrderItem orderItem : cart.getOrderItemList()) {
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                if (item.getNum()>=orderItem.getNum()){
                    if (item.getStockCount()!=null){
                        item.setStockCount(item.getStockCount()+orderItem.getNum());
                    }else {
                        item.setStockCount(0+orderItem.getNum());
                    }
                    item.setNum(item.getNum()-orderItem.getNum());
                    itemDao.updateByPrimaryKeySelective(item);
                }else {
                    throw new RuntimeException("该商品库存不足");
                }
            }
            long orderId = idWorker.nextId();//订单id
            tbOrder.setOrderId(orderId);
            tbOrder.setOutTradeNo(outTradeNo);//支付订单号,一个购物车列表整体对应一个订单
            tbOrder.setPaymentType(order.getPaymentType());//支付方式
            tbOrder.setStatus("1");//支付状态
            tbOrder.setCreateTime(new Date());//创建时间
            tbOrder.setUpdateTime(new Date());//修改时间
            tbOrder.setUserId(order.getUserId());//用户id
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货地址
            tbOrder.setReceiver(order.getReceiver());//收件人
            tbOrder.setReceiverMobile(order.getReceiverMobile());//电话
            tbOrder.setSourceType(order.getSourceType());//订单来源
            tbOrder.setSellerId(cart.getSellerId());//商家id
            //实付金额
            double money = 0;
            List<OrderItem> orderItemList = cart.getOrderItemList();
            if (orderItemList == null) {
                throw new RuntimeException("购物车内没东西");
            }
            for (OrderItem orderItem : orderItemList) {
                orderItem.setOrderId(orderId);
                orderItem.setId(idWorker.nextId());
                orderItemDao.insertSelective(orderItem);
                money += orderItem.getTotalFee().doubleValue();
            }
            totalMoney += (long) (money * 100);
            tbOrder.setPayment(new BigDecimal(money));
            orderDao.insertSelective(tbOrder);
        }
        //3.如果是微信支付,则在支付日志中添加记录
        if ("1".equals(order.getPaymentType())) {
            PayLog payLog = new PayLog();
            payLog.setCreateTime(new Date());
            payLog.setOutTradeNo(outTradeNo);
            payLog.setPayType(order.getPaymentType());
            payLog.setTotalFee(totalMoney);
            payLog.setUserId(order.getUserId());
            payLog.setTradeState("0");
            payLogDao.insertSelective(payLog);

            //将支付日志以用户名为key存入redis,以便修改订单支付状态
            redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
        }
        //4.清除购物车记录
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }

    /**
     * //TODO 待实现用户查询自己的订单
     * 根据用户名查询订单
     * @param userId
     * @return
     */
    @Override
    public List<Order> findOrderListByUserId(String userId) {
        return null;
    }

    /**
     * 根据用户名从redis中查询支付日志
     * @param userId
     * @return
     */
    @Override
    public PayLog findPayLogByUserIdFromRedis(String userId) {
        return (PayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    /**
     * 修改订单状态
     * @param out_trade_no  支付订单号
     * @param transaction_id    微信返回的交易流水号
     */
    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //1.修改支付日志状态及相关字段
        PayLog payLog = payLogDao.selectByPrimaryKey(out_trade_no);
        payLog.setTransactionId(transaction_id);
        payLog.setPayTime(new Date());
        payLog.setTradeState("1");
        payLogDao.updateByPrimaryKeySelective(payLog);
        //2.修改订单的状态
        Order order = new Order();
        order.setStatus("2");
        order.setPaymentTime(new Date());
        OrderQuery query = new OrderQuery();
        OrderQuery.Criteria criteria = query.createCriteria();
        criteria.andOutTradeNoEqualTo(out_trade_no);
        orderDao.updateByExampleSelective(order,query);
        //3.清除redis中支付日志
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }

    /**
     * //查出未付款订单
     * @param userId
     * @return
     */
    @Override
    public List<Order> findOrderListByUserIdUnPay(String userId) {
        return null;
    }

    /**
     * 运营商后台,查询订单数据,用于导出excel
     * @return
     */
    @Override
    public List<Order> getOrderList() {
        return orderDao.selectByExample(null);
    }
}
