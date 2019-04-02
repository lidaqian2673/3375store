package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 模板管理
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    //查询分页 有条件
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody TypeTemplate typeTemplate){
        return typeTemplateService.search(page,rows,typeTemplate);
    }
    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.add(typeTemplate);
             return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //查询一个模板对象
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }
    //用于订单统计的方法
    @RequestMapping("/findECharts")
    public Map findECharts(){
        return typeTemplateService.findECharts();
    }

    //查询分页 有条件 用于审核
    @RequestMapping("/searchAudit")
    public PageResult searchAudit(Integer page, Integer rows, @RequestBody TypeTemplate typeTemplate){
        return typeTemplateService.searchAudit(page,rows,typeTemplate);
    }
    //更改审核状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, Integer status){
        return typeTemplateService.updateStatus(ids,status);
    }

}
