package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 品牌管理
 */
@Service
public class BrandServiceImpl implements BrandService {



    @Autowired
    private BrandDao brandDao;

    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    //查询分页对象
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {

        //分页插件
        PageHelper.startPage(pageNum, pageSize);

        //查询结果集
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);
        //总条数
        //结果集 select * from tb_brand  limit 开始行,每页数
        return new PageResult(page.getTotal(), page.getResult());
    }

    //保存
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
        //insert into tb_tt (id,name,98个) values (3,haha,null 98个)  执行的效果是一样的 但是执行的效率是一样的
        //insert into tb_tt (id,name) values (3,haha)
        //update tb_tt set id = #{id},name= ......   where id
    }

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    //修改
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    //删除
    @Override
    public void deletes(Long[] ids) {
        //判断
        if(null != ids && ids.length > 0){
            for (Long id : ids) {
                brandDao.deleteByPrimaryKey(id);
            }
        }
        //批量删除  delete from tt_tt where id in (1,2,4,5) 动态Sql
        //BrandQuery brandQuery = new BrandQuery();
       // brandQuery.createCriteria().andIdIn(Arrays.asList(ids));  //数组换集合
        //brandDao.deleteByExample(brandQuery);


    }

    //条件的分页对象查询

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {

        //分页插件
        PageHelper.startPage(pageNum, pageSize);
        //条件对象
        BrandQuery brandQuery = new BrandQuery();
        //创建内部条件对象
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //判断名称是否有值
        if(null != brand.getName() && !"".equals(brand.getName().trim())){
            criteria.andNameLike("%"+brand.getName().trim()+"%");
        }
        //判断首字母
        if(null != brand.getFirstChar() && !"".equals(brand.getFirstChar().trim())){
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }

        //查询结果集
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
        //总条数
        //结果集 select * from tb_brand  limit 开始行,每页数
        return new PageResult(page.getTotal(), page.getResult());
    }

    //查询所有品牌 并返回
    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }

}
