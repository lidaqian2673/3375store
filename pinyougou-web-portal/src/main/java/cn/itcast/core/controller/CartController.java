package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Cart;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车管理  9003
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    //加入购物车
    @RequestMapping("/addGoodsToCartList")
    /*   @CrossOrigin(origins={"http://localhost:9103"},allowCredentials="true")*/
    @CrossOrigin(origins = {"http://localhost:9103"})
    public Result addToCart(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {

        try {
            List<Cart> cartList = null;
//            1:获取Cookie
            Cookie[] cookies = request.getCookies();
            if (null != cookies && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    //是购物车
                    if ("CART".equals(cookie.getName())) {
//            2：获取Cookie中购物车
                        //购物车对象  Cookie只能保存String类型 不能保存对象 将对象转成JSon格式字符串 取出串换回对象
                        cartList = JSON.parseArray(cookie.getValue(), Cart.class);
                    }
                }
            }

//           3:没有 创建购物车
            if (null == cartList) {
                cartList = new ArrayList<>();
            }

//          4：追加当前款
            Cart newCart = new Cart();

            Item item = cartService.findItemById(itemId);
            //商家Id
            newCart.setSellerId(item.getSellerId());
            //商家名称 不写 浪费
            //商家里商品结果集
            OrderItem newOrderItem = new OrderItem();
            //库存ID
            newOrderItem.setItemId(itemId);
//            数量
            newOrderItem.setNum(num);

            //创建集合
            List<OrderItem> newOrderItemList = new ArrayList<>();
            newOrderItemList.add(newOrderItem);
            newCart.setOrderItemList(newOrderItemList);



            //1)判断当前款商品的商家  是否在购物车集合中 众多商家中已存在
            int newIndexOf = cartList.indexOf(newCart);//indexOf  -1 不存在  >=0 存在同时 存在的角标位置

            if (newIndexOf != -1) {
                //--1:存在
                //2)判断当前款商品在此商家下众多商品中已存在
                Cart oldCart = cartList.get(newIndexOf);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                int indexOf = oldOrderItemList.indexOf(newOrderItem);
                if (indexOf != -1) {
                    //--1:存在  追加商品的数量
                    OrderItem oldOrderItem = oldOrderItemList.get(indexOf);
                    oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());
                } else {
                    //--2：不存在 新建一个商品并放到此商家下
                    oldOrderItemList.add(newOrderItem);
                }
            } else {
                //--2:不存在  直接创建新的购物车（因为一个购物车对应一个商家，并在此商家下创建新商品）
                cartList.add(newCart);
            }

            //判断当前是否登陆
            String name = SecurityContextHolder.getContext().getAuthentication().getName();

            if(!"anonymousUser".equals(name)){
                //登陆了
//                5：保存以上数据到Redis缓存中
                cartService.addCartListToRedis(cartList,name);
//                获取缓存中数据 合并之前 再保存到缓存中替换到之前缓存中的数据
//                        清空Cookie
                Cookie cookie = new Cookie("CART", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);


            }else{
                //未登陆
//           5:创建Cookie 保存购物车到Cookie 回写Cookie到浏览器
                Cookie cookie = new Cookie("CART", JSON.toJSONString(cartList));
                cookie.setMaxAge(60 * 60 * 24 * 3);
                cookie.setPath("/");
                //http://localhost:9003/shop/cart.do
                //http://localhost:9003/haha/ss.do
                response.addCookie(cookie);

            }






            return new Result(true, "加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "加入购物车失败");
        }
    }

    //查询所有购物车结果集
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {

        List<Cart> cartList = null;
//        1：获取Cookie
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("CART".equals(cookie.getName())) {
//        2：获取Cookie中购物车
                    cartList = JSON.parseArray(cookie.getValue(), Cart.class);
                }
            }
        }

        //判断当前是否登陆
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!"anonymousUser".equals(name)){
            //已登陆

//            3:有 追加购物车到缓存中
                if(null != cartList){
                    cartService.addCartListToRedis(cartList,name);
                    //                        清空Cookie
                    Cookie cookie = new Cookie("CART", null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
//            4：获取缓存中完整的购物车
            cartList = cartService.findCartListFromRedis(name);

        }


//        5：有  装满  //商家ID 库存ID 数量
        if (null != cartList) {
            cartList = cartService.findCartList(cartList);
        }
//        6：回显
        return cartList;

    }
}
