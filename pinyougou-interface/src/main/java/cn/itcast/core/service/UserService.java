package cn.itcast.core.service;

import cn.itcast.core.pojo.user.User;
import entity.PageResult;

public interface UserService {
    void sendCode(String phone);

    void add(User user, String smscode);

    void updateInfo(User user);

    PageResult selectUserList(Integer pageNum, Integer pageSize, User user);

    void updateStatus(Long[] ids, String status);
}
