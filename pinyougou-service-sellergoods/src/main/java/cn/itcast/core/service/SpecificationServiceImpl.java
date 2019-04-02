package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationAuditDao;
import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.specification.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.apache.zookeeper.data.Id;
import org.junit.internal.matchers.Each;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vo.SpecificationVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 规格管理
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationAuditDao specificationAuditDao;
    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    //条件 查询分页对象
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        PageHelper.startPage(page,rows);

        Page<SpecificationVo> p = (Page<SpecificationVo>) specificationDao.selectSpecWithAuditSelective(specification);

        return new PageResult(p.getTotal(),p.getResult());
    }


    //添加
    @Override
    public void add(SpecificationVo vo) {
        //规格表 并返回ID

        Specification specification = vo.getSpecification();
        specificationDao.insertSelective(specification);

        Long id = specification.getId();
        SpecificationAudit specificationAudit = new SpecificationAudit();
        specificationAudit.setId(id);
        specificationAudit.setAuditStatus(0);
        specificationAuditDao.insertSelective(specificationAudit);



        //规格选项表
        List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            //设置外键
            specificationOption.setSpecId(vo.getSpecification().getId());
            specificationOptionDao.insertSelective(specificationOption);
        }



    }

    //查询一个Vo对象
    @Override
    public SpecificationVo findOne(Long id) {
        SpecificationVo vo = new SpecificationVo();
        //规格
        vo.setSpecification(specificationDao.selectByPrimaryKey(id));
        //规格选项结果集
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(id);
        vo.setSpecificationOptionList(specificationOptionDao.selectByExample(query));

        return vo;
    }

    //修改
    @Override
    public void update(SpecificationVo vo) {
        //规格表
        specificationDao.updateByPrimaryKeySelective(vo.getSpecification());
        //规格选项表 多
        //1:先删除
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(vo.getSpecification().getId());
        specificationOptionDao.deleteByExample(query);
        //2:后添加
        List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            //设置外键
            specificationOption.setSpecId(vo.getSpecification().getId());
            specificationOptionDao.insertSelective(specificationOption);
        }

    }

    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }

    /**
     * 分页查询所有未审核的规格
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    @Override
    public PageResult findNotAuditSpecifications(Integer page, Integer rows, Specification specification) {

        //先查询所有规格审核表的id,此id与规格表的id一样,这样就作为查询规格表的条件
        SpecificationAuditQuery specificationAuditQuery = new SpecificationAuditQuery();
        specificationAuditQuery.createCriteria().andAuditStatusEqualTo(0);
        List<SpecificationAudit> specificationAudits = specificationAuditDao.selectByExample(specificationAuditQuery);
        //把未审核的id组装一个id的集合
        List<Long> specIds = new ArrayList<>();
        if (null != specificationAudits && specificationAudits.size() > 0) {
            for (SpecificationAudit specificationAudit : specificationAudits) {
                specIds.add(specificationAudit.getId());
            }
        }

        //分页助手一定要放在下面这查询前面,否则分页就是查询上面那个表了

        PageHelper.startPage(page, rows);
        //创建规格查询条件对象
        SpecificationQuery specificationQuery = new SpecificationQuery();
        //添加名字模糊与id范围

        SpecificationQuery.Criteria criteria = specificationQuery.createCriteria().andIdIn(specIds);
        if (null!=specification.getSpecName()&&!"".equals(specification.getSpecName().trim())){
            criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
        }

        Page<Specification> specifications = (Page<Specification>) specificationDao.selectByExample(specificationQuery);

        return new PageResult(specifications.getTotal(), specifications.getResult());
    }

    /**
     * 根据id批量审核规格,修改的是规格审核表
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        SpecificationAudit specificationAudit = new SpecificationAudit();
        specificationAudit.setAuditStatus(Integer.valueOf(status));
        if (null != ids && ids.length > 0) {
            for (Long id : ids) {
                //根据id批量更新规格审核表的状态,id同时也是主键
                specificationAudit.setId(id);
                specificationAuditDao.updateByPrimaryKeySelective(specificationAudit);
            }
        }



    }

    //删除规格
    @Override
    public void delete(Long[] ids) {
        if (null!=ids&&ids.length>0){
            for (Long id : ids) {
                specificationDao.deleteByPrimaryKey(id);
            }
        }

    }
}
