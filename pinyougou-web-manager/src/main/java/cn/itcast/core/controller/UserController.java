package cn.itcast.core.controller;

import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运营商后台用户管理
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 运营商后台查询用户列表
     * @param page
     * @param rows
     * @param user
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody User user) {
        System.out.println(111);
        return userService.selectUserList(page, rows, user);
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status) {
        try {
            userService.updateStatus(ids, status);
            return new Result(true, "更改状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更改状态失败");
        }
    }

}
