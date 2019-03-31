package cn.itcast.core.dao.good;

import cn.itcast.core.pojo.good.BrandAudit;
import cn.itcast.core.pojo.good.BrandAuditQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BrandAuditDao {
    int countByExample(BrandAuditQuery example);

    int deleteByExample(BrandAuditQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(BrandAudit record);

    int insertSelective(BrandAudit record);

    List<BrandAudit> selectByExample(BrandAuditQuery example);

    BrandAudit selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") BrandAudit record, @Param("example") BrandAuditQuery example);

    int updateByExample(@Param("record") BrandAudit record, @Param("example") BrandAuditQuery example);

    int updateByPrimaryKeySelective(BrandAudit record);

    int updateByPrimaryKey(BrandAudit record);
}