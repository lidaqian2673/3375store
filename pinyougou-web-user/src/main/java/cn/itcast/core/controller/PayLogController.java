package cn.itcast.core.controller;

import cn.itcast.core.service.PayLogService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import entity.Cart;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/order")
public class PayLogController {
    @Reference
    private PayLogService payLogService;
    @RequestMapping("/findOrderList")

    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response) {
        List<Cart> cartList = null;
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(name)){
            cartList=payLogService.findOrderListFromRedis(name);
        }

        return cartList;
    }
}
