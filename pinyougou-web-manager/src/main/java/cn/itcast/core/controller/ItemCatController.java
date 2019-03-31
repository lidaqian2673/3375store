package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<ItemCat> findByParentId(Long parentId){

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
}
