package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 品牌管理
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;
    //查询所有品牌结果集
    @RequestMapping("/findAll")
    public List<Brand> findAll(){
        return brandService.findAll();
    }

    //入参：
    //URL:
    //返回值
    @RequestMapping("/findPage")
    public PageResult findPage(Integer pageNum, Integer pageSize){
        return brandService.findPage(pageNum,pageSize);
    }
    //查询分页对象 入参： 当前页 每页数 条件对象 ?id=2
    @RequestMapping("/search")
    //public PageResult search(Integer pageNum, Integer pageSize,@RequestBody(required = false) Brand brand){
    public PageResult search(Integer pageNum, Integer pageSize,@RequestBody Brand brand){


        return brandService.search(pageNum,pageSize,brand);
    }
    //保存
    @RequestMapping("/add")
    public Result add(@RequestBody Brand brand){
        //保存
        try {
            brandService.add(brand);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"保存失败");
        }

    }
    //保存
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        //保存
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"修改失败");
        }

    }
    //删除
    @RequestMapping("/deletes")
    public Result deletes(Long[] ids){
        //保存
        try {
            brandService.deletes(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"删除失败");
        }

    }
    //查询一个品牌
    @RequestMapping("/findOne")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }

    //查询所有品牌 并返回List<Map>
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
