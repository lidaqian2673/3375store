package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车管理
 */
@SuppressWarnings("all")
@Service
@Transactional
public class CartServiceImpl implements  CartService {



    @Autowired
    private ItemDao itemDao;
    //准备工作：根据库存ID查询商家的ID
    @Override
    public Item findItemById(Long itemId) {


        return itemDao.selectByPrimaryKey(itemId);
    }

    //将购物车装满
    @Override
    public List<Cart> findCartList(List<Cart> cartList) {
        for (Cart cart : cartList) {
            //商家名称

            //商品结果集
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                //有的数据是： 库存ID 数量
                Item item = findItemById(orderItem.getItemId());
                //图片
                orderItem.setPicPath(item.getImage());
                //标题
                orderItem.setTitle(item.getTitle());
                //价格
                orderItem.setPrice(item.getPrice());
                //小计
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*orderItem.getNum()));

                //商家名称
                cart.setSellerName(item.getSeller());
            }


        }
        return cartList;
    }


    @Autowired
    private RedisTemplate redisTemplate;
    //将Cookie与当前款合并后的购物车追加到缓存中
    @Override
    public void addCartListToRedis(List<Cart> newCartList,String name) {
        //1:从缓存中取出购物车

        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("cart").get(name);
        //2:将新车合并老车中
        oldCartList =  mergeCartList(newCartList,oldCartList);
        //3:将老车保存到缓存中
        redisTemplate.boundHashOps("cart").put(name,oldCartList);



    }

    @Override
    public List<Cart> findCartListFromRedis(String name) {
        return (List<Cart>) redisTemplate.boundHashOps("cart").get(name);
    }

    //将新车合并老车中
    public List<Cart> mergeCartList(List<Cart> newCartList,List<Cart> oldCartList){

        //1:只有一个新车  合并到老车集合中
        //2：现在是新车集合 合并到老车集合中

        //准备工作
        if(null != newCartList && newCartList.size() >0){
            if(null != oldCartList && oldCartList.size() >0){
                //合并
                for (Cart newCart : newCartList) {
                    //1)判断当前款商品的商家  是否在购物车集合中 众多商家中已存在
                    int newIndexOf = oldCartList.indexOf(newCart);//indexOf  -1 不存在  >=0 存在同时 存在的角标位置

                    if (newIndexOf != -1) {
                        //--1:存在
                        //2)判断当前款商品在此商家下众多商品中已存在
                        Cart oldCart = oldCartList.get(newIndexOf);
                        List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();

                        //新商品是集合
                        List<OrderItem> newOrderItemList = newCart.getOrderItemList();
                        for (OrderItem newOrderItem : newOrderItemList) {
                            int indexOf = oldOrderItemList.indexOf(newOrderItem);
                            if (indexOf != -1) {
                                //--1:存在  追加商品的数量
                                OrderItem oldOrderItem = oldOrderItemList.get(indexOf);
                                oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());
                            } else {
                                //--2：不存在 新建一个商品并放到此商家下
                                oldOrderItemList.add(newOrderItem);
                            }
                        }
                    } else {
                        //--2:不存在  直接创建新的购物车（因为一个购物车对应一个商家，并在此商家下创建新商品）
                        oldCartList.add(newCart);
                    }

                }
            }else{
                return newCartList;
            }
        }
        return oldCartList;
    }

}
