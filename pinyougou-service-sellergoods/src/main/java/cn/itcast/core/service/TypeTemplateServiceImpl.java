package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 模板管理
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    @Autowired
    private RedisTemplate redisTemplate;

    //查询分页对象
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        //1:将模板结果集从Mysql查询出来 保存缓存 一份
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        //2:通过模板ID 查询
        for (TypeTemplate template : typeTemplates) {
            //通过模板ID 查询  List<Map>  品牌结果集
            List<Map> brandList = JSON.parseArray(template.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
            //通过模板ID 查询  List<Map>  规格结果集
            List<Map> specList = findBySpecList(template.getId());
            redisTemplate.boundHashOps("specList").put(template.getId(),specList);

        }
        PageHelper.startPage(page,rows);
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(null);
        return new PageResult(p.getTotal(),p.getResult());
    }

    //添加
    @Override
    public void add(TypeTemplate typeTemplate) {
         typeTemplateDao.insertSelective(typeTemplate);
    }

    //查询一个模板对象
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    //修改
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }


    //通过模板ID 查询List<Map>  Map 长度3 id text options规格选项结果集
    @Override
    public List<Map> findBySpecList(Long id) {
        //1:模板ID
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        List<Map> listMap = JSON.parseArray(specIds, Map.class);
        for (Map map : listMap) {//object
            //id
            //text
            //options
            SpecificationOptionQuery query = new SpecificationOptionQuery();
                                      //报错：Object 能不能直接强转成Long  不能
                                      // 基本类型 String Integer   长整型Long
                                      // Object可以先转成基本类型 再强转成长整型    强行的话：报错类型转换异常
            query.createCriteria().andSpecIdEqualTo((long)(Integer)map.get("id"));
            map.put("options",specificationOptionDao.selectByExample(query));
        }

        return listMap;
    }
}
