package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.ItemCatVo;

import java.util.List;
import java.util.Map;

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
    //查询所有
    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }

    //    查询未审核分类集合
    @RequestMapping("/findAuditList")
    public List<Map> findAuditList(){
        return itemCatService.findAuditList();
    }

    //更改审核状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, Integer status){
        return itemCatService.updateStatus(ids,status);
    }


}
