package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单管理
 */
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
    private ItemDao itemDao;

    @Autowired
    private PayLogDao payLogDao;

    //保存订单
    @Override
    public void add(Order order) {

        //收货人  地址  手机  支付方式


        //根据用户名查询此用户的购物车集合（长度）
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cart").get(order.getUserId());

        //很多订单的金额之和
        long payLogTotal = 0;
        //订单ID集合
        List<Long> ids = new ArrayList<>();

        for (Cart cart : cartList) {

            //订单ID    分布式ID
            long id = idWorker.nextId();
            ids.add(id);
            order.setOrderId(id);

            //实付金额
            double total = 0;


            //支付状态
            order.setStatus("1");
            //订单创建时间
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            //订单来源
            order.setSourceType("2");
            //商家ID
            order.setSellerId(cart.getSellerId());


            //多个订单详情表
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {

                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());

                //订单详情表ID
                long orderItemId =  idWorker.nextId();
                orderItem.setId(orderItemId);
                //商品ID
                orderItem.setGoodsId(item.getGoodsId());
                //订单ID
                orderItem.setOrderId(id);
                //标题
                orderItem.setTitle(item.getTitle());
                //价格
                orderItem.setPrice(item.getPrice());
                //总金额  小计
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));

                //追加总金额
                total += orderItem.getTotalFee().doubleValue();
                //图片
                orderItem.setPicPath(item.getImage());
                //商家ID
                orderItem.setSellerId(item.getSellerId());

                //保存
                orderItemDao.insertSelective(orderItem);
            }

            //设置订单的总金额
            order.setPayment(new BigDecimal(total));

            payLogTotal += order.getPayment().longValue();

            //保存订单
            orderDao.insertSelective(order);

        }

        //日志表  （将上面的所有订单合并起来  一起付钱）
        PayLog payLog = new PayLog();
        //ID
        payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
        //日志产生 时间
        payLog.setCreateTime(new Date());
        //总金额
        payLog.setTotalFee(payLogTotal*100);

        //用户ID
        payLog.setUserId(order.getUserId());

        //支付状态
        payLog.setTradeState("0");

        //订单集合  [341,32414213,2131412]
        payLog.setOrderList(ids.toString().replace("[","").replace("]",""));
        //支付方式
        payLog.setPayType("1");

        payLogDao.insertSelective(payLog);
        //保存缓存一份
        redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);

        //购物车
        redisTemplate.boundHashOps("cart").delete(order.getUserId());

    }
}
