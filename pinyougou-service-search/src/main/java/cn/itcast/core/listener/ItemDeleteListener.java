package cn.itcast.core.listener;


import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;


/**
 * 消息 处理类 自定义的
 */
public class ItemDeleteListener implements MessageListener {


    @Autowired
    private SolrTemplate solrTemplate;
    //接收消息的方法
    @Override
    public void onMessage(Message message) {
        //商品ID
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;

        try {
            String id = atm.getText();
            System.out.println("搜索项目删除时要接收到的ID：" + id);
            //2:将商品信息从索引库中删除出去
            SolrDataQuery query = new SimpleQuery(new Criteria("item_goodsid").is(id));
            solrTemplate.delete(query);
            solrTemplate.commit();


        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
