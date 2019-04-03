package cn.itcast.core.controller;

import cn.itcast.core.service.OrderTableService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.OrderTable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orderTable")
public class OrderTableController {
    @Reference
    private OrderTableService orderTableService;

    @RequestMapping("/findorderTable")
    public List<OrderTable> findorderTable(){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderTableService.findorderTable(sellerId);
    }

}
