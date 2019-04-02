package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateAuditDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateAudit;
import cn.itcast.core.pojo.template.TypeTemplateAuditQuery;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import vo.TypeTemplateVo;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 模板管理
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private TypeTemplateAuditDao typeTemplateAuditDao;

    //查询分页对象
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        //1:将模板结果集从Mysql查询出来 保存缓存 一份
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        //2:通过模板ID 查询
        for (TypeTemplate template : typeTemplates) {
            //通过模板ID 查询  List<Map>  品牌结果集
            List<Map> brandList = JSON.parseArray(template.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
            //通过模板ID 查询  List<Map>  规格结果集
            List<Map> specList = findBySpecList(template.getId());
            redisTemplate.boundHashOps("specList").put(template.getId(),specList);

        }
        PageHelper.startPage(page,rows);
        Page<TypeTemplateVo> p = (Page<TypeTemplateVo>) typeTemplateDao.selectSpecWithAuditSelective(typeTemplate);
        return new PageResult(p.getTotal(),p.getResult());
    }

    //添加
    @Override
    public void add(TypeTemplate typeTemplate) {
         typeTemplateDao.insertSelective(typeTemplate);
         //商家申请
        Long id = typeTemplate.getId();
        TypeTemplateAudit typeTemplateAudit = new TypeTemplateAudit();
        typeTemplateAudit.setId(id);
        typeTemplateAudit.setTypeTemplateAuditStatus(0);
        typeTemplateAuditDao.insertSelective(typeTemplateAudit);

    }

    //查询一个模板对象
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    //修改
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }


    //通过模板ID 查询List<Map>  Map 长度3 id text options规格选项结果集
    @Override
    public List<Map> findBySpecList(Long id) {
        //1:模板ID
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        List<Map> listMap = JSON.parseArray(specIds, Map.class);
        for (Map map : listMap) {//object
            //id
            //text
            //options
            SpecificationOptionQuery query = new SpecificationOptionQuery();
                                      //报错：Object 能不能直接强转成Long  不能
                                      // 基本类型 String Integer   长整型Long
                                      // Object可以先转成基本类型 再强转成长整型    强行的话：报错类型转换异常
            query.createCriteria().andSpecIdEqualTo((long)(Integer)map.get("id"));
            map.put("options",specificationOptionDao.selectByExample(query));
        }

        return listMap;
    }
    //用于订单统计的方法
    @Override
    public Map findECharts() {
        HashMap<Object, Object> resultMap = new HashMap<>();

        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<Map> mapList = new ArrayList<>();
        //获取近两个星期日期的集合字符串
        for (int i = 13; i >= 0; i--) {
            dateList.add(getFrontDay(new Date(),i));
        }
        //获取所有一级分类的name属性集合
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(0l);
        List<ItemCat> itemCats = itemCatDao.selectByExample(itemCatQuery);
        for (ItemCat itemCat : itemCats) {
            nameList.add(itemCat.getName());
        }
        //获取每个分类订单的两个星期的销售额嵌套集合
        for (ItemCat itemCat : itemCats) {
            HashMap<String, Object> mingqianHashMap = new HashMap<>();
            ArrayList<Integer> moneyList = new ArrayList<>();

            GoodsQuery goodsQuery = new GoodsQuery();
            goodsQuery.createCriteria().andCategory1IdEqualTo(itemCat.getId());
            List<Goods> goods = goodsDao.selectByExample(goodsQuery);
            //获取某一天的订单钱数
            Integer money = 0;
            for (String date : dateList) {
                money = 0;
            //获取属于某一天的订单id
                List<Long> OrderIds = new ArrayList<>();
//                获取所有订单
                List<Order> orders = orderDao.selectByExample(null);
//                如果订单日期是这天就将订单id添加进去
                for (Order order : orders) {
                    if (getDay(order.getCreateTime())==Integer.parseInt(date.substring(3))){
                        OrderIds.add(order.getOrderId());
                    }
                }

                if (goods!=null&& goods.size()>0) {
                    for (Goods goodss : goods) {

                        List<OrderItem> orderItems = null;
//                        属于当天的订单的订单项
                        if (null!=OrderIds&&OrderIds.size()>0) {
                            OrderItemQuery orderItemQuery = new OrderItemQuery();
                            OrderItemQuery.Criteria criteria = orderItemQuery.createCriteria();
                            criteria.andGoodsIdEqualTo(goodss.getId());
                            criteria.andOrderIdIn(OrderIds);
                            //属于某一天该分类的某一库存所有订单项
                            orderItems = orderItemDao.selectByExample(orderItemQuery);
                        }

                        if (null!=orderItems && orderItems.size()>0) {
                            for (OrderItem orderItem : orderItems) {
                                money+=(int)orderItem.getTotalFee().doubleValue();
                            }
                        }
                    }
                }
                moneyList.add(money);
//                Random random = new Random();
//                moneyList.add(random.nextInt(30));
            }
                mingqianHashMap.put("name",itemCat.getName());
                mingqianHashMap.put("moneyList",moneyList);
                mapList.add(mingqianHashMap);
        }

        resultMap.put("dateList",dateList);
        resultMap.put("nameList",nameList);
        resultMap.put("mapList",mapList);

        return resultMap;
    }

    //返回时间属于哪一天(日)
    private Integer getDay(Date date){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE));
        Date time = cal.getTime();
        return time.getDay();
    }

    //返回某个日期前几天的日期
    private  String getFrontDay(Date date, int i) {
        Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
        ca.setTime(date); // 设置时间为当前时间
        ca.add(Calendar.DATE, -i);// 日期减1
        Date resultDate = ca.getTime(); // 结果
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String s = sdf.format(resultDate).toString();
        return s;
    }

    @Override
    public PageResult searchAudit(Integer page, Integer rows, TypeTemplate typeTemplate) {

        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = typeTemplateQuery.createCriteria();
//        判断模板名称是否为空，根据模板名称条件查询
        if (null!=typeTemplate&&!"".equals(typeTemplate)){
            if (null!=typeTemplate.getName()&&!"".equals(typeTemplate.getName())){
                criteria.andNameLike("%"+typeTemplate.getName()+"%");
            }
        }
//        只查询未审核的
//        在审核表查询未审核的id集合
        TypeTemplateAuditQuery typeTemplateAuditQuery = new TypeTemplateAuditQuery();
        typeTemplateAuditQuery.createCriteria().andTypeTemplateAuditStatusEqualTo(0);
        List<TypeTemplateAudit> typeTemplateAudits = typeTemplateAuditDao.selectByExample(typeTemplateAuditQuery);
        List<Long> ids = new ArrayList<>();
        for (TypeTemplateAudit typeTemplateAudit : typeTemplateAudits) {
            ids.add(typeTemplateAudit.getId());
        }

        criteria.andIdIn(ids);
        PageHelper.startPage(page,rows);
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public Result updateStatus(Long[] ids, Integer status) {
        try {
            TypeTemplateAudit typeTemplateAudit = new TypeTemplateAudit();

            typeTemplateAudit.setTypeTemplateAuditStatus(status);

            for (Long id : ids){
                typeTemplateAudit.setId(id);
              typeTemplateAuditDao.updateByPrimaryKeySelective(typeTemplateAudit);
            }
            return new Result(true,"审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"审核失败");
        }
    }

    //删除
    @Override
    public void delete(Long[] ids) {
        if (null!=ids&&ids.length>0){
            for (Long id : ids) {
                typeTemplateDao.deleteByPrimaryKey(id);
            }
        }
    }
}
