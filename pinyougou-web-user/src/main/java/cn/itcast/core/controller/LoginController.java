package cn.itcast.core.controller;

import cn.itcast.core.service.PayLogService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Cart;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登陆管理
 */
@RestController
@RequestMapping("/login")
public class LoginController {
@Reference
private PayLogService payLogService;
    //当前登陆人 用户名
    @RequestMapping("/name")
    public Map<String,Object> showName(HttpServletRequest request){



        //使用SecurityContextHolder 工具类 获取用户名或是用户名对象 当前线程
        String username2 = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String,Object> map = new HashMap<>();
        map.put("loginName",username2);
        return map;
    }
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
