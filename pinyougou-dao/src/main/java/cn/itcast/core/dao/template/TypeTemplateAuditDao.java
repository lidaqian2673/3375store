package cn.itcast.core.dao.template;

import cn.itcast.core.pojo.template.TypeTemplateAudit;
import cn.itcast.core.pojo.template.TypeTemplateAuditQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TypeTemplateAuditDao {
    int countByExample(TypeTemplateAuditQuery example);

    int deleteByExample(TypeTemplateAuditQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(TypeTemplateAudit record);

    int insertSelective(TypeTemplateAudit record);

    List<TypeTemplateAudit> selectByExample(TypeTemplateAuditQuery example);

    TypeTemplateAudit selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TypeTemplateAudit record, @Param("example") TypeTemplateAuditQuery example);

    int updateByExample(@Param("record") TypeTemplateAudit record, @Param("example") TypeTemplateAuditQuery example);

    int updateByPrimaryKeySelective(TypeTemplateAudit record);

    int updateByPrimaryKey(TypeTemplateAudit record);
}