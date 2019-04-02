package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.log.PayLogQuery;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import entity.Cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PayLogServiceImpl implements PayLogService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private PayLogDao payLogDao;
    @Autowired
    private ItemDao itemDao;


    @Override
    public List<Cart> findOrderListFromRedis(String name) {

        List<PayLog> payLogs = (List<PayLog>) redisTemplate.boundHashOps("payLogs").get(name);
        if (null == payLogs || 0 == payLogs.size()) {
            //本地查询
            PayLogQuery payLogQuery = new PayLogQuery();
            payLogQuery.createCriteria().andUserIdEqualTo(name);
            List<PayLog> payLogList = payLogDao.selectByExample(payLogQuery);
            redisTemplate.boundHashOps("payLogs").put(name,payLogList);
            List<Cart> carts = findOrdersFromPaylog(payLogList);
            return carts;
        }else {
            List<Cart> cartsFromPaylog = findOrdersFromPaylog(payLogs);
            return cartsFromPaylog;
        }
    }

    public List<Cart> findOrdersFromPaylog(List<PayLog> payLogs) {
        List<Cart> cartArrayList = new ArrayList<>();
        for (PayLog payLog : payLogs) {
            Date payTime = payLog.getPayTime();
            String tradeState = payLog.getTradeState();

            //获取订单号集合
            List<String> orderList = Arrays.asList(payLog.getOrderList().split(","));
            //获取单个订单
            for (String s : orderList) {
                Cart cart = new Cart();
                //设置订单号
                cart.setOut_trade_no(s);

                long order_id = Long.parseLong(s.trim());

                //根据订单ID查询订单详情
                OrderItemQuery orderItemQuery = new OrderItemQuery();
                orderItemQuery.createCriteria().andOrderIdEqualTo(order_id);

                //设置订单状态
                if ("1".equals(tradeState)) {
                    tradeState = "已付款";
                }
                if ("0".equals(tradeState)) {
                    tradeState = "未付款";
                }
                cart.setTrade_state(tradeState);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = sdf.format(payTime);
                cart.setPayTime(format);

                //订单结果集
                List<OrderItem> orderItems = orderItemDao.selectByExample(orderItemQuery);
                cart.setOrderItemList(orderItems);
                OrderItem orderItem = orderItems.get(0);
                String sellerId = orderItem.getSellerId();
                Long itemId = orderItem.getItemId();
                Item item = itemDao.selectByPrimaryKey(itemId);
                //订单商家名字
                cart.setSellerName(item.getSeller());
                //订单商家ID
                cart.setSellerId(sellerId);

                cartArrayList.add(cart);
            }
        }
        return cartArrayList;

    }
}
