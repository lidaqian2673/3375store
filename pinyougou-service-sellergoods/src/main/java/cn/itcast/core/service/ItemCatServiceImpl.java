package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类管理
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<ItemCat> findByParentId(Long parentId) {

        //1:从Mysql数据库将所有数据查询出来 保存到缓存中一份
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        //2:保存到缓存中  五大数据类型hash

        for (ItemCat itemCat : itemCats) {
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
        }

        //正常商品分类列表查询
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
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
        List<Map> maps = new ArrayList<>();

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
        return maps;
    }
}
