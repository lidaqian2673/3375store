package cn.itcast.core.service;

import cn.itcast.core.pojo.item.ItemCat;
import entity.Result;
import vo.ItemCatVo;

import java.util.List;
import java.util.Map;

public interface ItemCatService {
    List<ItemCatVo> findByParentId(Long parentId);

    ItemCat findOne(Long id);

    List<ItemCat> findAll();

    List<Map> findItemCatList();

    List<Map> findAuditList();

    Result updateStatus(Long[] ids, Integer status);

    //保存
    void add(ItemCat itemCat);
}
