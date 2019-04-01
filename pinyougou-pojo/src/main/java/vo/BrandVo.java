package vo;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandAudit;

import java.io.Serializable;

/**
 * 一个包含了品牌与品牌状态的包装类
 */
public class BrandVo implements Serializable{
    private Brand brand;
    private BrandAudit brandAudit;

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public BrandAudit getBrandAudit() {
        return brandAudit;
    }

    public void setBrandAudit(BrandAudit brandAudit) {
        this.brandAudit = brandAudit;
    }
}
