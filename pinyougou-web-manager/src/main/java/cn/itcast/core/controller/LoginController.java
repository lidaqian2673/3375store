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
    @RequestMapping("/showName")
    public Map<String,Object> showName(HttpServletRequest request){
        //1:从Session中获取出来
        SecurityContext spring_security_context = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        //用户对象
        User user = (User) spring_security_context.getAuthentication().getPrincipal();
        String username1 = user.getUsername();
        System.out.println("从Session中获取的用户对象中再次获取的用户名：" + username1);
        //直接获取用户对象中的用户名
        String username = spring_security_context.getAuthentication().getName();
        System.out.println("从Session中直接获取的用户名：" + username);
        //2:使用SecurityContextHolder 工具类 获取用户名或是用户名对象 当前线程
        String username2 = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("从当前线程中获取的用户名：" + username2);
        //3:jsp页面  ${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.username}
        //4:jsp页面  <security:authentication  name="principal.username" />
        Map<String,Object> map = new HashMap<>();
        map.put("username",username2);
        map.put("curTime",new Date());
        return map;
    }
}
