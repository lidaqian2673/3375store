package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import vo.GoodsVo;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品管理
 */
@SuppressWarnings("all")
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private BrandDao brandDao;

    //添加三张表
    @Override
    public void add(GoodsVo vo) {
        //商品表
        //1:页面传递过来的
        //2:程序手动写的  添加时间

        //状态  未审核
        vo.getGoods().setAuditStatus("0");
        //保存   回显商品ID
        goodsDao.insertSelective(vo.getGoods());

        //商品详情表 商品ID 使用上面商品表生成的主键

        vo.getGoodsDesc().setGoodsId(vo.getGoods().getId());
        goodsDescDao.insertSelective(vo.getGoodsDesc());

        //是否启用规格
        if ("1".equals(vo.getGoods().getIsEnableSpec())) {
            //库存结果集
            List<Item> itemList = vo.getItemList();
            for (Item item : itemList) {
                //{"机身内存":"16G","网络":"联通3G","",""}
                String spec = item.getSpec();
                // [{},{}]
                //标题 商品名称 + " " + 规格1 + " " + 规格2 + " " ...
                String title = vo.getGoods().getGoodsName();
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title += " " + entry.getValue();
                }
                item.setTitle(title);

                //图片  从商品表一堆图片中 第一张
                //[{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]
                String itemImages = vo.getGoodsDesc().getItemImages();
                List<Map> images = JSON.parseArray(itemImages, Map.class);
                if (null != images && images.size() > 0) {

                    item.setImage((String) images.get(0).get("url"));
                }
                //商品分类 三级分类的Id
                item.setCategoryid(vo.getGoods().getCategory3Id());

                //三级分类的名称
                item.setCategory(itemCatDao.selectByPrimaryKey(vo.getGoods().getCategory3Id()).getName());
                //添加时间
                item.setCreateTime(new Date());
                //更新时间
                item.setUpdateTime(new Date());

                //商品ID
                item.setGoodsId(vo.getGoods().getId());
                //商家ID
                item.setSellerId(vo.getGoods().getSellerId());
                //商家名称
                item.setSeller(sellerDao.selectByPrimaryKey(vo.getGoods().getSellerId()).getNickName());
                //品牌名称
                item.setBrand(brandDao.selectByPrimaryKey(vo.getGoods().getBrandId()).getName());

                //保存一条库存表数据
                itemDao.insertSelective(item);
            }

        } else {
            //不启用 （不写）
        }

    }

    //查询分页对象（运营商  商家后台）
    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {

        PageHelper.startPage(page, rows);
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();

        //审核状态
        if (null != goods.getAuditStatus() && !"".equals(goods.getAuditStatus())) {
            criteria.andAuditStatusEqualTo(goods.getAuditStatus());
        }
        //商品名称
        if (null != goods.getGoodsName() && !"".equals(goods.getGoodsName().trim())) {
            criteria.andGoodsNameLike("%" + goods.getGoodsName().trim() + "%");
        }
        //（运营商与商家都应该只显示不删除的商品）
        criteria.andIsDeleteIsNull();
        //只能查询当前登陆人 他家的商品
        //如果是商家后台调用  goods里面就会有当前登陆人  如果是运营商 就没有当前登陆人
        if (null != goods.getSellerId()) {
            criteria.andSellerIdEqualTo(goods.getSellerId());
        }

        //查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);

        return new PageResult(p.getTotal(), p.getResult());
    }

    //根据商品ID查询一个包装对象
    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo vo = new GoodsVo();
        //商品对象
        vo.setGoods(goodsDao.selectByPrimaryKey(id));
        //商品详情对象
        vo.setGoodsDesc(goodsDescDao.selectByPrimaryKey(id));
        //库存结果集对象
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        vo.setItemList(itemDao.selectByExample(itemQuery));
        return vo;
    }

    //修改
    @Override
    public void update(GoodsVo vo) {
        //商品表
        goodsDao.updateByPrimaryKeySelective(vo.getGoods());
        //商品详情表
        goodsDescDao.updateByPrimaryKeySelective(vo.getGoodsDesc());
        //库存结果集表
        //1:先通过商品ID 外键 整体删除
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(vo.getGoods().getId());
        itemDao.deleteByExample(itemQuery);
        //2:添加
        //是否启用规格
        if ("1".equals(vo.getGoods().getIsEnableSpec())) {
            //库存结果集
            List<Item> itemList = vo.getItemList();
            for (Item item : itemList) {
                //{"机身内存":"16G","网络":"联通3G","",""}
                String spec = item.getSpec();
                // [{},{}]
                //标题 商品名称 + " " + 规格1 + " " + 规格2 + " " ...
                String title = vo.getGoods().getGoodsName();
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title += " " + entry.getValue();
                }
                item.setTitle(title);

                //图片  从商品表一堆图片中 第一张
                //[{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]
                String itemImages = vo.getGoodsDesc().getItemImages();
                List<Map> images = JSON.parseArray(itemImages, Map.class);
                if (null != images && images.size() > 0) {

                    item.setImage((String) images.get(0).get("url"));
                }
                //商品分类 三级分类的Id
                item.setCategoryid(vo.getGoods().getCategory3Id());

                //三级分类的名称
                item.setCategory(itemCatDao.selectByPrimaryKey(vo.getGoods().getCategory3Id()).getName());
                //添加时间
                item.setCreateTime(new Date());
                //更新时间
                item.setUpdateTime(new Date());

                //商品ID
                item.setGoodsId(vo.getGoods().getId());
                //商家ID
                item.setSellerId(vo.getGoods().getSellerId());
                //商家名称
                item.setSeller(sellerDao.selectByPrimaryKey(vo.getGoods().getSellerId()).getNickName());
                //品牌名称
                item.setBrand(brandDao.selectByPrimaryKey(vo.getGoods().getBrandId()).getName());

                //保存一条库存表数据
                itemDao.insertSelective(item);
            }

        } else {
            //不启用 （不写）
        }

    }



    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicPageAndSolrDestination;
    @Autowired
    private Destination queueSolrDeleteDestination;
    //开始审核
    @Override
    public void updateStatus(Long[] ids, String status) {
        Goods goods = new Goods();
        goods.setAuditStatus(status);
        //商品表的ID
        for (final Long id : ids) {
            goods.setId(id);
            //1：更新状态  审核通过  不通过
            goodsDao.updateByPrimaryKeySelective(goods);
            //判断只能在审核通过的情况下 才能保存商品信息到索引库
            if("1".equals(status)){
                //发消息 jms
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return  session.createTextMessage(String.valueOf(id));
                    }
                });


            }

        }
    }

    //删除
    @Override
    public void delete(Long[] ids) {
        Goods goods = new Goods();
        goods.setIsDelete("1");
        for (final Long id : ids) {
            goods.setId(id);
            //1:更新商品的是否删除字段为1
            goodsDao.updateByPrimaryKeySelective(goods);
            //发消息
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return  session.createTextMessage(String.valueOf(id));
                }
            });


        }
    }
}
