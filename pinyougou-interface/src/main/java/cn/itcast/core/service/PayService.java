package cn.itcast.core.service;

import cn.itcast.core.pojo.log.PayLog;

import java.util.List;
import java.util.Map;

public interface PayService {
    Map<String,String> createNative(String name);

    Map<String,String> queryPayStatus(String out_trade_no);

    void update(String name, String transaction_id);

}
