package cn.itcast.core.dao.specification;

import cn.itcast.core.pojo.specification.SpecificationAudit;
import cn.itcast.core.pojo.specification.SpecificationAuditQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpecificationAuditDao {
    int countByExample(SpecificationAuditQuery example);

    int deleteByExample(SpecificationAuditQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(SpecificationAudit record);

    int insertSelective(SpecificationAudit record);

    List<SpecificationAudit> selectByExample(SpecificationAuditQuery example);

    SpecificationAudit selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SpecificationAudit record, @Param("example") SpecificationAuditQuery example);

    int updateByExample(@Param("record") SpecificationAudit record, @Param("example") SpecificationAuditQuery example);

    int updateByPrimaryKeySelective(SpecificationAudit record);

    int updateByPrimaryKey(SpecificationAudit record);
}