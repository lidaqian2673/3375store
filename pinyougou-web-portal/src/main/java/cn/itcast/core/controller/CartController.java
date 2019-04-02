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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9103", allowCredentials = "true")
    public Result addToCart(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Cart> cartList = null;
            Cookie[] cookies = request.getCookies();
            //判断
            if (null != cookies && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    //获取cookie中的购物车
                    //cookie 解码格式
                    if ("cart".equals(cookie.getName())) {
                        String decode = URLDecoder.decode(cookie.getValue(), "utf-8");
                        cartList = JSON.parseArray(decode, Cart.class);
                    }
                }
            }
            if (null == cartList) {
                cartList = new ArrayList<>();
            }
            // 追加当前款
            Cart newCart = new Cart();
            Item item = cartService.findItemById(itemId);
            newCart.setSellerId(item.getSellerId());

            OrderItem orderItem = new OrderItem();
            orderItem.setNum(num);
            orderItem.setItemId(itemId);

            List<OrderItem> newOrderItemList = new ArrayList<>();
            newOrderItemList.add(orderItem);

            newCart.setOrderItemList(newOrderItemList);

            int newCartIndex = cartList.indexOf(newCart);
            if (-1 != newCartIndex) {
                Cart oldCart = cartList.get(newCartIndex);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                int orderIndexOf = oldOrderItemList.indexOf(orderItem);
                if (-1 != orderIndexOf) {
                    OrderItem oldOrderItem = oldOrderItemList.get(orderIndexOf);
                    oldOrderItem.setNum(oldOrderItem.getNum() + orderItem.getNum());
                } else {
                    oldOrderItemList.add(orderItem);
                }
            } else {
                cartList.add(newCart);
            }
            //判断是否登录
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            if(!"anonymousUser".equals(name)){

                cartService.addCartListToRedis(cartList,name);

                Cookie cookie = new Cookie("cart",null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);

            }else {
                String s = JSON.toJSONString(cartList).trim();
                String encode = URLEncoder.encode(s, "utf-8");
                Cookie cookie = new Cookie("cart", encode);
                cookie.setMaxAge(60*60*24);
                cookie.setPath("/");
                response.addCookie(cookie);
            }


            return new Result(true, "加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "加入购物车失败");
        }
    }


    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        List<Cart> cartList = null;
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("cart".equals(cookie.getName())) {
                    String decode = null;
                    try {
                        decode = URLDecoder.decode(cookie.getValue(), "utf-8");
                        cartList = JSON.parseArray(decode, Cart.class);
                        String name = SecurityContextHolder.getContext().getAuthentication().getName();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(name)){
            if (null != cartList){
                cartService.addCartListToRedis(cartList,name);
                Cookie cookie = new Cookie("cart",null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
            cartList=cartService.findCartListFromRedis(name);
        }
        if (null != cartList) {
            cartList = cartService.findCartList(cartList);
        }
        return cartList;
    }
}
