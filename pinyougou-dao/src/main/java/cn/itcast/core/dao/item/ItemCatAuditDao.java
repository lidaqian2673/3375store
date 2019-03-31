package cn.itcast.core.dao.item;

import cn.itcast.core.pojo.item.ItemCatAudit;
import cn.itcast.core.pojo.item.ItemCatAuditQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemCatAuditDao {
    int countByExample(ItemCatAuditQuery example);

    int deleteByExample(ItemCatAuditQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ItemCatAudit record);

    int insertSelective(ItemCatAudit record);

    List<ItemCatAudit> selectByExample(ItemCatAuditQuery example);

    ItemCatAudit selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ItemCatAudit record, @Param("example") ItemCatAuditQuery example);

    int updateByExample(@Param("record") ItemCatAudit record, @Param("example") ItemCatAuditQuery example);

    int updateByPrimaryKeySelective(ItemCatAudit record);

    int updateByPrimaryKey(ItemCatAudit record);
}