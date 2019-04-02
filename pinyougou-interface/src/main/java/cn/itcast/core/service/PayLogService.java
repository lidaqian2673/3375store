package cn.itcast.core.service;

import entity.Cart;

import java.util.List;

public interface PayLogService {
    List<Cart> findOrderListFromRedis(String name);
}
