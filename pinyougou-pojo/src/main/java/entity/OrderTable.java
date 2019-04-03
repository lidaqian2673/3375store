package entity;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import org.apache.poi.common.usermodel.LineStyle;

import java.io.Serializable;
import java.util.List;

public class OrderTable implements Serializable{
    private Order order;
    private List<OrderItem> orderItemList;


    public OrderTable() {
    }

    public OrderTable( Order order, List<OrderItem> orderItemList) {
        this.order = order;
        this.orderItemList = orderItemList;
    }


    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
