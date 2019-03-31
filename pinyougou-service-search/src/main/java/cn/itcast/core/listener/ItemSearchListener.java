package cn.itcast.core.listener;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;

/**
 * 消息 处理类 自定义的
 */
public class ItemSearchListener implements MessageListener {


    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;
    //接收消息的方法
    @Override
    public void onMessage(Message message) {
        //商品ID
        ActiveMQTextMessage atm = (ActiveMQTextMessage)message;

        try {
            String id = atm.getText();
            System.out.println("搜索项目接收到的ID：" + id);


            //2:将此商品信息保存到索引库  //商品ID + 是否默认 = 1条 或 4条
            ItemQuery itemQuery = new ItemQuery();
            itemQuery.createCriteria().andGoodsIdEqualTo(Long.parseLong(id)).andIsDefaultEqualTo("1");
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            solrTemplate.saveBeans(itemList,1000);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
