package cn.itcast.core.service;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家管理
 */
@Service
@Transactional
public class SellerServiceImpl implements  SellerService {

    @Autowired
    private SellerDao sellerDao;
    @Override
    public void add(Seller seller) {

        //用户名
        //密码
        seller.setPassword(
                new BCryptPasswordEncoder().encode(seller.getPassword()));
        //公司名
        //店铺
        //状态 未审核
        seller.setStatus("0");


        sellerDao.insertSelective(seller);
    }

    //查询商家对象  通过用户名
    @Override
    public Seller findSellerByUsername(String username) {

        return sellerDao.selectByPrimaryKey(username);
    }

    //


}
