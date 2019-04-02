package vo;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateAudit;

import java.io.Serializable;

public class TypeTemplateVo implements Serializable{
    private TypeTemplate typeTemplate;
    private TypeTemplateAudit typeTemplateAudit;

    public TypeTemplate getTypeTemplate() {
        return typeTemplate;
    }

    public void setTypeTemplate(TypeTemplate typeTemplate) {
        this.typeTemplate = typeTemplate;
    }

    public TypeTemplateAudit getTypeTemplateAudit() {
        return typeTemplateAudit;
    }

    public void setTypeTemplateAudit(TypeTemplateAudit typeTemplateAudit) {
        this.typeTemplateAudit = typeTemplateAudit;
    }
}
