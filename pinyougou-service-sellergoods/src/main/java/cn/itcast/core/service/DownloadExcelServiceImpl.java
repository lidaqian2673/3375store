package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class DownloadExcelServiceImpl implements DownloadExcelService {
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private TypeTemplateDao typeTemplateDao;
    @Autowired
    private ItemCatDao itemCatDao;

    @Override
    public Object downloadExcelByTableName(String tableName) {
        if ("tb_brand".equals(tableName)) {
            return brandDao.selectByExample(null);
        }else if ("tb_specification".equals(tableName)) {
            return specificationDao.selectByExample(null);
        }else if ("tb_type_template".equals(tableName)) {
            return typeTemplateDao.selectByExample(null);
        } else if ("tb_item_cat".equals(tableName)) {
            return itemCatDao.selectByExample(null);
        } else {
            throw new RuntimeException("无数据");
        }
    }

}
