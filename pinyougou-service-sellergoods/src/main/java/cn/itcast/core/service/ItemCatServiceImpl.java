package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatAuditDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatAudit;
import cn.itcast.core.pojo.item.ItemCatAuditQuery;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import vo.ItemCatVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类管理
 */
@SuppressWarnings("all")
@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ItemCatAuditDao itemCatAuditDao;
    @Override
    public List<ItemCatVo> findByParentId(Long parentId) {

        //1:从Mysql数据库将所有数据查询出来 保存到缓存中一份
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        //2:保存到缓存中  五大数据类型hash

        for (ItemCat itemCat : itemCats) {
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
        }

        //正常商品分类列表查询
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);

        //商家后台分类申请
        List<ItemCat> itemCats1 = itemCatDao.selectByExample(itemCatQuery);
        List<ItemCatVo>itemCatVoList=new ArrayList<>();
        for (ItemCat itemCat : itemCats1) {
            ItemCatVo itemCatVo = new ItemCatVo();
            itemCatVo.setItemCat(itemCat);
            ItemCatAudit itemCatAudit = itemCatAuditDao.selectByPrimaryKey(itemCat.getId());
            itemCatVo.setItemCatAudit(itemCatAudit);
            itemCatVoList.add(itemCatVo);
        }

        return itemCatVoList;

    }


    //查询一个
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    //查询商品分类信息
    @Override
    public List<Map> findItemCatList() {
//        从缓存取出商品分类信息
        List<Map> maps = (List<Map>) redisTemplate.boundValueOps("ItemCatList").get();

//        缓存没有就从数据库查一份放进去
        if (null == maps||maps.size()==0){
            maps = new ArrayList<>();

            //        查询父id为0的分类
            ItemCatQuery itemCatQuery = new ItemCatQuery();
            itemCatQuery.createCriteria().andParentIdEqualTo(0l);
            List<ItemCat> itemCats = itemCatDao.selectByExample(itemCatQuery);
//        遍历所有顶层分类分别与下级分类组成map
            for (ItemCat itemCat : itemCats) {
                HashMap<String, Object> upperMap = new HashMap<>();
                List<Map> lowerMaps = new ArrayList<>();
                ItemCatQuery itemCatQuery1 = new ItemCatQuery();
                itemCatQuery1.createCriteria().andParentIdEqualTo(itemCat.getId());
                List<ItemCat> itemCats1 = itemCatDao.selectByExample(itemCatQuery1);
//            遍历所有中层分类分别与下级分类组成map
                for (ItemCat cat : itemCats1) {
                    ArrayList<String> list = new ArrayList<>();
                    HashMap<String, Object> lowerMap = new HashMap<>();
                    ItemCatQuery itemCatQuery2 = new ItemCatQuery();
                    itemCatQuery2.createCriteria().andParentIdEqualTo(cat.getId());
                    List<ItemCat> itemCats2 = itemCatDao.selectByExample(itemCatQuery2);
                    for (ItemCat itemCat1 : itemCats2) {
                        list.add(itemCat1.getName());
                    }
                    lowerMap.put("itemCatName",cat.getName());
                    lowerMap.put("itemCatList",list);
                    lowerMaps.add(lowerMap);
                }
                upperMap.put("ItemCatName",itemCat.getName());
                upperMap.put("ItemCatList",lowerMaps);
                maps.add(upperMap);
            }

            redisTemplate.boundValueOps("ItemCatList").set(maps);
        }
        return maps;
    }

//    查询未审核分类集合
    @Override
    public List<Map> findAuditList() {
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();
//        只查询未审核的
//        在审核表查询未审核的id集合
        ItemCatAuditQuery itemCatAuditQuery = new ItemCatAuditQuery();
        itemCatAuditQuery.createCriteria().andItemCatAuditStatusEqualTo(0);
        List<ItemCatAudit> itemCatAudits = itemCatAuditDao.selectByExample(itemCatAuditQuery);
        List<Long> ids = new ArrayList<>();
        for (ItemCatAudit itemCatAudit : itemCatAudits) {
            ids.add(itemCatAudit.getId());
        }

        criteria.andIdIn(ids);
        List<ItemCat> itemCats = itemCatDao.selectByExample(itemCatQuery);
        ArrayList<Map> maps = new ArrayList<>();

        for (ItemCat itemCat : itemCats) {
            HashMap<String, Object> resultMap = new HashMap<>();
//            通过父id查询name属性
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(itemCat.getParentId());
            resultMap.put("id",itemCat.getId());
            resultMap.put("name",itemCat.getName());
            resultMap.put("typeId",itemCat.getTypeId());
            resultMap.put("parent",itemCat1.getName());
            maps.add(resultMap);
        }
        return maps;
    }

    @Override
    public Result updateStatus(Long[] ids, Integer status) {
        try {
            ItemCatAudit itemCatAudit = new ItemCatAudit();

            itemCatAudit.setItemCatAuditStatus(status);

            for (Long id : ids){
                itemCatAudit.setId(id);
                itemCatAuditDao.updateByPrimaryKeySelective(itemCatAudit);
            }
            return new Result(true,"审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"审核失败");
        }
    }
    //保存
    @Override
    public void add(ItemCat itemCat) {

        itemCatDao.insertSelective(itemCat);
        Long id = itemCat.getId();
        ItemCatAudit itemCatAudit = new ItemCatAudit();
        itemCatAudit.setId(id);
        itemCatAudit.setItemCatAuditStatus(0);
        itemCatAuditDao.insertSelective(itemCatAudit);
    }

    //删除
    @Override
    public void delete(Long[] ids) {
        if (null!=ids&&ids.length>0){
            for (Long id : ids) {
                itemCatDao.deleteByPrimaryKey(id);
            }
        }
    }
}
