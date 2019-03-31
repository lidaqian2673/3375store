package cn.itcast.core.pojo.item;

import java.io.Serializable;

public class ItemCatAudit implements Serializable {
    private Long id;

    private Integer itemCatAuditStatus;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItemCatAuditStatus() {
        return itemCatAuditStatus;
    }

    public void setItemCatAuditStatus(Integer itemCatAuditStatus) {
        this.itemCatAuditStatus = itemCatAuditStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", itemCatAuditStatus=").append(itemCatAuditStatus);
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
        ItemCatAudit other = (ItemCatAudit) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getItemCatAuditStatus() == null ? other.getItemCatAuditStatus() == null : this.getItemCatAuditStatus().equals(other.getItemCatAuditStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getItemCatAuditStatus() == null) ? 0 : getItemCatAuditStatus().hashCode());
        return result;
    }
}