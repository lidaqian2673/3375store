package cn.itcast.core.service;

import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import entity.OrderTable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderTableServiceImpl implements OrderTableService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Override
    public List<OrderTable> findorderTable(String sellerId) {
        List<OrderTable> orderTableList = new ArrayList<>();

        OrderQuery orderQuery = new OrderQuery();
        orderQuery.createCriteria().andSellerIdEqualTo(sellerId);
        List<Order> orders = orderDao.selectByExample(orderQuery);

        for (Order order : orders) {
            OrderTable orderTable = new OrderTable();
            Order orderT = new Order();
            orderT.setCreateTime(order.getCreateTime());
            orderT.setOrderId(order.getOrderId());
            orderT.setSellerId(order.getSellerId());
            orderT.setReceiverMobile(order.getReceiverMobile());
            orderT.setReceiverAreaName(order.getReceiverAreaName());
            orderT.setReceiver(order.getReceiver());
            OrderItem orderItemT = new OrderItem();
            OrderItemQuery orderItemQuery = new OrderItemQuery();
            orderItemQuery.createCriteria().andOrderIdEqualTo(orderT.getOrderId());
            List<OrderItem> orderItems = orderItemDao.selectByExample(orderItemQuery);
            List<OrderItem> orderItemList = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                orderItemT.setPicPath(orderItem.getPicPath());
                orderItemT.setTitle(orderItem.getTitle());
                orderItemT.setPrice(orderItem.getPrice());
                orderItemT.setNum(orderItem.getNum());
                orderItemT.setTotalFee(orderItem.getTotalFee());
                orderItemList.add(orderItemT);
            }
            orderTable.setOrderItemList(orderItemList);
            orderTable.setOrder(orderT);
            orderTableList.add(orderTable);
        }


        return orderTableList;
    }
}
