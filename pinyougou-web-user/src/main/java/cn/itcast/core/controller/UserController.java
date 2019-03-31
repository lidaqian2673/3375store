package cn.itcast.core.controller;

import cn.itcast.common.utils.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 * 用户注册
 * 用户登陆
 * 用户个人中心
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Reference
    private UserService userService;
    //发送验证码
    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        //判断手机号是否合法
        if(PhoneFormatCheckUtils.isPhoneLegal(phone)){
            try {
                userService.sendCode(phone);
                return new Result(true,"发送成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false,"发送失败");
            }
        }else{
            return new Result(false,"手机号不合法");
        }


    }
    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody User user, String smscode){

            try {
                //if(null user.getUsername())


                userService.add(user,smscode);
                return new Result(true,"注册成功");
            } catch (RuntimeException e) {
                return new Result(false,e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false,"注册失败");
            }



    }
}
