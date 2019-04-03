package vo;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatAudit;

import java.io.Serializable;

public class ItemCatVo implements Serializable{
    private ItemCat itemCat;
    private ItemCatAudit itemCatAudit;

    public ItemCat getItemCat() {
        return itemCat;
    }

    public void setItemCat(ItemCat itemCat) {
        this.itemCat = itemCat;
    }

    public ItemCatAudit getItemCatAudit() {
        return itemCatAudit;
    }

    public void setItemCatAudit(ItemCatAudit itemCatAudit) {
        this.itemCatAudit = itemCatAudit;
    }
}
