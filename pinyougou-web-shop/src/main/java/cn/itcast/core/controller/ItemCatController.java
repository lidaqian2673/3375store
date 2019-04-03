package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.ItemCatVo;

import javax.xml.ws.RequestWrapper;
import java.util.List;

/**
 * 商品分类管理
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {


    @Reference
    private ItemCatService itemCatService;
    //查询所有二级分类
    @RequestMapping("/findByParentId")
    public List<ItemCatVo> findByParentId(Long parentId){

        //Long parentId  0 查询所有一级分类
        //Long parentId  1 查询父ID为1的所有二级分页
        //Long parentId  2 查询父ID为2的所有三级分页
        return itemCatService.findByParentId(parentId);

    }
    //查询一个商品分类
    @RequestMapping("/findOne")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }
    //查询所有
    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody ItemCat itemCat) {
        //保存
        try {
            itemCatService.add(itemCat);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false, "保存失败");
        }
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        //删除
        try {
            itemCatService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"删除失败");
        }

    }
}
