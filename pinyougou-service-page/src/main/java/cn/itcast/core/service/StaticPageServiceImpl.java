package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 静态化处理实现类
 */
@Service
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {


    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemCatDao itemCatDao;

    //静态化商品详情页面的方法    解决乱码问题？

    public void index(Long id) {
        // 1：创建Freemarker实现类
        Configuration conf = freeMarkerConfigurer.getConfiguration();

        //输出流
        Writer out = null;
        //输出路径  全路径
        String path = getPath("/" + id + ".html");

        // 3:加载模板  页面上有标签 读取到内存中  磁盘上  读取到内存 IO流 编码读取的啊
        try {

            Template template = conf.getTemplate("item.ftl");

            //输出流  从内存到磁盘  UTF-8
            out = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");

            //数据
            Map<String,Object> root = new HashMap<>();

            //1:根据商品表的ID查询库存结果集
            ItemQuery itemQuery = new ItemQuery();
            itemQuery.createCriteria().andGoodsIdEqualTo(id);
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            root.put("itemList",itemList);

            //2:查询商品详情表 ID
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
            root.put("goodsDesc",goodsDesc);
            //3:商品表
            Goods goods = goodsDao.selectByPrimaryKey(id);
            root.put("goods",goods);

            //4:商品分类名称  1级 2级 3级
            root.put("itemCat1",itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName());
            root.put("itemCat2",itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName());
            root.put("itemCat3",itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());



            //4：处理
            template.process(root,out);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != out){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //方法 获取全路径
    public String getPath(String path){
        return servletContext.getRealPath(path);
    }

    //注入进来一个ServletContext
    private ServletContext servletContext;
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
