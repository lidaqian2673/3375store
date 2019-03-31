package cn.itcast.core.pojo.template;

import java.io.Serializable;

public class TypeTemplateAudit implements Serializable {
    private Long id;

    private Integer typeTemplateAuditStatus;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTypeTemplateAuditStatus() {
        return typeTemplateAuditStatus;
    }

    public void setTypeTemplateAuditStatus(Integer typeTemplateAuditStatus) {
        this.typeTemplateAuditStatus = typeTemplateAuditStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", typeTemplateAuditStatus=").append(typeTemplateAuditStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TypeTemplateAudit other = (TypeTemplateAudit) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTypeTemplateAuditStatus() == null ? other.getTypeTemplateAuditStatus() == null : this.getTypeTemplateAuditStatus().equals(other.getTypeTemplateAuditStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTypeTemplateAuditStatus() == null) ? 0 : getTypeTemplateAuditStatus().hashCode());
        return result;
    }
}