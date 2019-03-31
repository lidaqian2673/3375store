package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登陆管理
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    //当前登陆人 用户名
    @RequestMapping("/name")
    public Map<String,Object> showName(HttpServletRequest request){



        //使用SecurityContextHolder 工具类 获取用户名或是用户名对象 当前线程
        String username2 = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String,Object> map = new HashMap<>();
        map.put("loginName",username2);
        //map.put("curTime",new Date());
        return map;
    }
}
