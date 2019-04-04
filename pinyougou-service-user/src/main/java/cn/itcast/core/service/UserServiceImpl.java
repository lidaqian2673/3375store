package cn.itcast.core.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理
 */
@Service
@Transactional
public class UserServiceImpl implements  UserService {


    //发消息 Map
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private Destination smsDestination;
    @Autowired
    private UserDao userDao;

    @Override
    public void sendCode(final String phone) {
        //1:生成验证码
        final String randomNumeric = RandomStringUtils.randomNumeric(6);
        //2:保存验证码到缓存中
        redisTemplate.boundValueOps(phone).set(randomNumeric);
        //redisTemplate.boundValueOps(phone).expire(1, TimeUnit.MINUTES);
        //2:发消息 Map
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                MapMessage map = session.createMapMessage();
                //手机号
                map.setString("iphone",phone);//"17801040609"
                //验证码
                map.setString("templateParam","{'number':'"+randomNumeric+"'}");
                //签名
                map.setString("signName","品优购商城");
                //模板ID
                map.setString("templateCode","SMS_126462276");


                return map;
            }
        });
    }

    //添加
    @Override
    public void add(User user, String smscode) {
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        //判断验证码是否失效
        if(null == code){
            throw new RuntimeException("验证码失败");
        }

        if(code.equals(smscode)){
            //保存用户信息
            user.setCreated(new Date());
            user.setUpdated(new Date());
            //密码加密


            userDao.insertSelective(user);


        }else{
            throw new RuntimeException("验证码不正确");
        }



    }

    @Override
    public void updateInfo(User user) {
        userDao.insertSelective(user);
    }

    /**
     * 查询用户列表
     * @param pageNum
     * @param pageSize
     * @param user
     * @return
     */
    @Override
    public PageResult selectUserList(Integer pageNum, Integer pageSize, User user) {
        PageHelper.startPage(pageNum, pageSize);
        UserQuery userQuery = new UserQuery();
        UserQuery.Criteria criteria = userQuery.createCriteria();
        if (null != user.getUsername() && user.getUsername().trim().equals("")) {
            criteria.andUsernameLike("%" + user.getUsername() + "%");
        }
        if (null != user.getStatus() && !user.getStatus().trim().equals("")) {
            criteria.andStatusEqualTo(user.getStatus());
        }
        Page<User> users = (Page<User>) userDao.selectByExample(userQuery);
        return new PageResult(users.getTotal(),users.getResult());
    }

    /**
     * 更改用户状态,冻结,解冻
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        User user = new User();
        user.setStatus(status);
        if (null != ids && ids.length > 0) {
            for (Long id : ids) {
                user.setId(id);
                userDao.updateByPrimaryKeySelective(user);
            }
        }

    }


}
