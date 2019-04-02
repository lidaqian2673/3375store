package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import entity.PageResult;

import java.util.List;

public interface OrderService {
    void add(Order order);

    PageResult search(Integer page, Integer rows, OrderItem orderItem);


}
