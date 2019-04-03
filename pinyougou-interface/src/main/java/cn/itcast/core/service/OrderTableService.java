package cn.itcast.core.service;

import entity.OrderTable;

import java.util.List;

public interface OrderTableService {
    List<OrderTable> findorderTable(String sellerId);
}
