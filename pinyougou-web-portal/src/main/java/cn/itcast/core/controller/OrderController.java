package cn.itcast.core.controller;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.RequestWrapper;

/**
 * 订单管理
 */
@RestController
@RequestMapping("/order")
public class OrderController {


    @Reference
    private OrderService orderService;
    //提交订单
    @RequestMapping("/add")
    public Result add(@RequestBody Order order){
        //收货人  地址  手机  支付方式
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(name);
            orderService.add(order);
            return new Result(true,"提交订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交订单失败");
        }

    }
}
