package cn.itcast.core.controller;

import cn.itcast.core.service.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.RequestWrapper;
import java.util.Map;

/**
 * 支付管理
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;
    //获取订单ID 金额 二维码的URL
    @RequestMapping("/createNative")
    public Map<String,String> createNative(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return payService.createNative(name);


    }
    //查询订单状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        try {
            int x = 0;
            //无限循环
            while (true){
                Map<String,String> map = payService.queryPayStatus(out_trade_no);
                if("NOTPAY".equals(map.get("trade_state"))){
                    //休息一会
                    Thread.sleep(5000);
                    x++;
                    if(x >= 60){
                        return new Result(false,"支付超时");
                    }
                }else{
                    //收尾：改状态
                    //map : 支付成功之后流水号
                    //支付成功的时间

                    return new Result(true,"支付成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }
}
