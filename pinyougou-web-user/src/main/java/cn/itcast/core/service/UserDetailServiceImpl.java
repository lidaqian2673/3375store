package cn.itcast.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义实现类 授权
 */
public class UserDetailServiceImpl implements UserDetailsService{


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取用户名 此用户已经登陆成功   中央认证系统那边 Session也没有失效 给此用户授权

        //根据用户名去查询
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username,"",authorities);
    }
}
