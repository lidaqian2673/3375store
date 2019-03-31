package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 安全框架 加载数据库中用户信息的实现类
 * 授权 类
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService{


    //将SellerService 远程调用
    private SellerService sellerService;
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    //加载数据库中用户信息的实现类 授权
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //通过用户名查询数据库 （数据库Mysql） 连接Mysql
        Seller seller = sellerService.findSellerByUsername(username);
        //判断是否有此用户
        if(null != seller){
            //有  用户 但判断状态 只有为1时审核通过时才让使用
            if("1".equals(seller.getStatus())){
                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
                return new User(seller.getSellerId(),seller.getPassword(),authorities);
            }


        }
        //无此用户
        return null;
    }
}
