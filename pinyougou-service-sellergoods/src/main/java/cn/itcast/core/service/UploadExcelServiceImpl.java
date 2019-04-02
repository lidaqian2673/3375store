package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandAuditDao;
import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.item.ItemCatAuditDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationAuditDao;
import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.template.TypeTemplateAuditDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandAudit;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatAudit;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationAudit;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateAudit;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UploadExcelServiceImpl implements UploadExcelService {

    @Override
    public void addExcelToTable(String name, List<List<Object>> data) {
        if ("品牌表".equals(name)) {
            addExcelToBrand(data);
        } else if ("规格表".equals(name)) {
            addExcelToSpecification(data);
        } else if ("模板表".equals(name)) {
            addExcelToTypeTemplate(data);
        } else if ("分类表".equals(name)) {
            addExcelToItemCat(data);
        }
    }


    /**
     * 将品牌表excel读取到的数据写入品牌表中
     */
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private BrandAuditDao brandAuditDao;

    public void addExcelToBrand(List<List<Object>> data) {
        for (List<Object> list : data) {
            Brand brand = new Brand();
            brand.setName((String) list.get(0));
            brand.setFirstChar((String) list.get(1));
            //将brand插入数据库
            brandDao.insertSelective(brand);
            //将状态插入状态表
            BrandAudit brandAudit = new BrandAudit();
            brandAudit.setId(brand.getId());
            brandAudit.setBrandStatus(0);
            brandAuditDao.insertSelective(brandAudit);
        }
    }

    /**
     * 将规格表excel读取到的数据保存到规格表
     */
    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationAuditDao specificationAuditDao;

    private void addExcelToSpecification(List<List<Object>> data) {

        for (List<Object> datum : data) {
            Specification specification = new Specification();
            //将规格插入数据库
            specification.setSpecName((String) datum.get(0));
            specificationDao.insertSelective(specification);
            //将规格状态插入数据库
            SpecificationAudit specificationAudit = new SpecificationAudit();
            specificationAudit.setId(specification.getId());
            specificationAudit.setAuditStatus(0);
            specificationAuditDao.insertSelective(specificationAudit);
        }
    }

    /**
     * 将模板表excel读取到的数据保存到模板表
     *
     * @param data
     */
    @Autowired
    private TypeTemplateDao typeTemplateDao;
    private TypeTemplateAuditDao typeTemplateAuditDao;
    private void addExcelToTypeTemplate(List<List<Object>> data) {
        for (List<Object> datum : data) {
            TypeTemplate typeTemplate = new TypeTemplate();
            typeTemplate.setName((String) datum.get(0));
            typeTemplate.setSpecIds((String) datum.get(1));
            typeTemplate.setBrandIds((String) datum.get(2));
            typeTemplate.setCustomAttributeItems((String) datum.get(3));
            typeTemplateDao.insertSelective(typeTemplate);

            //保存状态
            TypeTemplateAudit typeTemplateAudit = new TypeTemplateAudit();
            typeTemplateAudit.setId(typeTemplate.getId());
            typeTemplateAudit.setTypeTemplateAuditStatus(0);
            typeTemplateAuditDao.insertSelective(typeTemplateAudit);
        }
    }


    /**
     * 将分类表excel读取到的数据保存到分类表
     *
     * @param data
     */
    private ItemCatDao itemCatDao;
    private ItemCatAuditDao itemCatAuditDao;
    private void addExcelToItemCat(List<List<Object>> data) {
        for (List<Object> datum : data) {
            ItemCat itemCat = new ItemCat();
            itemCat.setParentId((Long) datum.get(1));
            itemCat.setName((String) datum.get(1));
            itemCat.setTypeId((Long) datum.get(2));
            itemCatDao.insertSelective(itemCat);
            //保存状态
            ItemCatAudit itemCatAudit = new ItemCatAudit();
            itemCatAudit.setId(itemCat.getId());
            itemCatAudit.setItemCatAuditStatus(0);
            itemCatAuditDao.insertSelective(itemCatAudit);
        }
    }


}
