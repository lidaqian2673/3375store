package cn.itcast.core.controller;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.SpecificationVo;

import java.util.List;
import java.util.Map;

/**
 * 规格管理
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    //条件对象的查询分页结果集
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification){
        return specificationService.search(page,rows,specification);
    }
    //保存
    @RequestMapping("/add")
    public Result add(@RequestBody SpecificationVo vo){
        try {
            specificationService.add(vo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody SpecificationVo vo){
        try {
            specificationService.update(vo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //查询一个规格 ID
    @RequestMapping("/findOne")
    public SpecificationVo findOne(Long id){
        return specificationService.findOne(id);
    }

    //查询所有规格结果集 返回值List<Map>
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return specificationService.selectOptionList();
    }


    //分页查询所有未审核的规格
    @RequestMapping("/findNotAuditSpec")
    public PageResult findNotAuditSpecifications(Integer page, Integer rows, @RequestBody Specification specification) {
        return specificationService.findNotAuditSpecifications(page,rows,specification);
    }

    //根据id批量审核规格
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status) {
        try {
            specificationService.updateStatus(ids, status);
            return new Result(true, "审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败");
        }
    }
    //删除
    @RequestMapping("/delete")
    public Result delete(Long [] ids){
        try {
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
