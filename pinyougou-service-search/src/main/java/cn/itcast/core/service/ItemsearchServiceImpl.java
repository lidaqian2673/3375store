package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

/**
 * 搜索管理
 * Spring事务：Mysql数据库有事务
 *            Solr索引库 （手动控制事务） 1000
 */
@SuppressWarnings("all")
@Service
public class ItemsearchServiceImpl implements ItemsearchService {

    //索引库
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    //搜索 开始
    //入参： 关键词  商品分类  品牌  规格（2）网络内存 价格区间  综合 新品  价格由高到低  价格由低到高
    // $scope.searchMap={'keywords':'   三   星 手  机  ','category':'手机','brand':'联想','spec':{'网络':'移动3G','内存':'16G'},'price':'0-500','pageNo':1,'pageSize':20,'sort':'','sortField':''};
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        Map<String,Object> resultMap = new HashMap<>();

        //提前处理关键词
        String keywords = searchMap.get("keywords");
        searchMap.put("keywords",keywords.replaceAll(" ",""));

        //1:商品分类结果集
        List<String> categoryList = searchCategoryListByKeywords(searchMap);
        resultMap.put("categoryList", categoryList);
        //2:品牌结果集
        //3:规格结果集
        if(null != categoryList && categoryList.size() > 0){
            resultMap.putAll(searchBrandListAndSpecListByCategory(categoryList.get(0)));
        }

        //4:查询结果集 总条数 总页数
        resultMap.putAll(search2(searchMap));

        return resultMap;
    }

    //2:品牌结果集
    //3:规格结果集
    public Map<String,Object> searchBrandListAndSpecListByCategory(String category){
        Map<String,Object> resultMap = new HashMap<>();


        //1:通过分类名称查询模板ID
        Object typeId = redisTemplate.boundHashOps("itemCat").get(category);
        //2:通过模板ID查询品牌结果集
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        //3:通过模板ID查询规格结果集
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);

        resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);
        return resultMap;

    }

    //查询商品分类
    public List<String> searchCategoryListByKeywords(Map<String,String> searchMap){
        //关键词
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        Query query = new SimpleQuery(criteria);
        // select * from 表 group by id
        GroupOptions groupOptions = new GroupOptions();
        //设置按照 分组域的
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //查询商品分类 分组查询
        GroupPage<Item> page = solrTemplate.queryForGroupPage(query, Item.class);

        List<String> categoryList = new ArrayList<>();

        //获取page的分组信息
        GroupResult<Item> categorys = page.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = categorys.getGroupEntries();
        List<GroupEntry<Item>> content = groupEntries.getContent();
        if(null != content && content.size() > 0){

            for (GroupEntry<Item> itemGroupEntry : content) {
                categoryList.add(itemGroupEntry.getGroupValue());
            }
        }

        return categoryList;

    }
    //查询结果集 总条数 总页数
    public Map<String,Object> search2(Map<String,String> searchMap){
        Map<String, Object> resultMap = new HashMap<>();
         //关键词
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
       // Query query = new SimpleHighlightQuery(criteria);
        //高亮的条件对象
        HighlightQuery highlightQuery = new SimpleHighlightQuery(criteria);
        //高亮的域
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        //前 缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //后 缀
        highlightOptions.setSimplePostfix("</em>");

        highlightQuery.setHighlightOptions(highlightOptions);

        //分页
        String pageNo = searchMap.get("pageNo");
        String pageSize = searchMap.get("pageSize");
        //开始行
        highlightQuery.setOffset((Integer.parseInt(pageNo) - 1)*Integer.parseInt(pageSize));
        //每页数
        highlightQuery.setRows(Integer.parseInt(pageSize));

        //过滤条件
        //商品分类
        if(null != searchMap.get("category") && !"".equals(searchMap.get("category"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            filterQuery.addCriteria(new Criteria("item_category").is(searchMap.get("category")));
            highlightQuery.addFilterQuery(filterQuery);
        }
        //品牌
        if(null != searchMap.get("brand") && !"".equals(searchMap.get("brand"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            filterQuery.addCriteria(new Criteria("item_brand").is(searchMap.get("brand")));
            highlightQuery.addFilterQuery(filterQuery);
        }
        // $scope.searchMap={'spec':{'网络':'移动3G','内存':'16G'},'sort':'','sortField':''};
        //规格
        if(null != searchMap.get("spec") && searchMap.get("spec").length() > 0){

            //{'网络':'移动3G','内存':'16G'}
            Map<String,String> specMap = JSON.parseObject(searchMap.get("spec"), Map.class);
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                                                //item_spec_网络": "联通3G",
                filterQuery.addCriteria(new Criteria("item_spec_" +  entry.getKey()).is( entry.getValue()));
                highlightQuery.addFilterQuery(filterQuery);
            }


        }

        //价格区间  0-500  3000-*
        if(null != searchMap.get("price") && !"".equals(searchMap.get("price"))){
            //String转成数组
            String[] p = searchMap.get("price").split("-");
            FilterQuery filterQuery = new SimpleFilterQuery();

            if(searchMap.get("price").contains("*")){
                //包含*
                filterQuery.addCriteria(new Criteria("item_price").greaterThanEqual(p[0]));
            }else{
                //不包含*
                filterQuery.addCriteria(new Criteria("item_price").between(p[0],p[1],true,true));

            }

            highlightQuery.addFilterQuery(filterQuery);
        }
        // $scope.searchMap={'sort':'DESC或ASC','sortField':'price或是updatetime'};
        //排序
        //判断
        if(null != searchMap.get("sort") && !"".equals(searchMap.get("sort"))){

            //判断是升还是降
            if("ASC".equals(searchMap.get("sort"))){
                //Ctrl + D
                 highlightQuery.addSort(new Sort(Sort.Direction.ASC,"item_" + searchMap.get("sortField")));
            }else{
                 highlightQuery.addSort(new Sort(Sort.Direction.DESC,"item_" + searchMap.get("sortField")));

            }
        }



        //查询高亮结果集
        HighlightPage<Item> page = solrTemplate.queryForHighlightPage(highlightQuery, Item.class);

        //获取高亮结果集
        List<HighlightEntry<Item>> highlighted = page.getHighlighted();
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //entity
            Item entity = itemHighlightEntry.getEntity();
            //highlights
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if(null != highlights && highlights.size() > 0){
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }


        //分页后的结果集   默认10条  普通结果集
        resultMap.put("rows",page.getContent());
        //总条数
        resultMap.put("total",page.getTotalElements());
        //总页数
        resultMap.put("totalPages",page.getTotalPages());


        return resultMap;
    }
    //查询结果集 总条数 总页数
    public Map<String,Object> search1(Map<String,String> searchMap){
        Map<String, Object> resultMap = new HashMap<>();


        //定义搜索对象的结构  category:商品分类
        // $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':20,'sort':'','sortField':''};

        //关键词
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        Query query = new SimpleQuery(criteria);

        //分页
        String pageNo = searchMap.get("pageNo");
        String pageSize = searchMap.get("pageSize");
        //开始行
        query.setOffset((Integer.parseInt(pageNo) - 1)*Integer.parseInt(pageSize));
        //每页数
        query.setRows(Integer.parseInt(pageSize));


        //过滤条件
        //商品分类
        //品牌
        //规格
        //价格区间

        //排序

        //查询普通分页结果集
        ScoredPage<Item> page = solrTemplate.queryForPage(query, Item.class);

        //分页后的结果集   默认10条
        resultMap.put("rows",page.getContent());
        //总条数
        resultMap.put("total",page.getTotalElements());
        //总页数
        resultMap.put("totalPages",page.getTotalPages());
        return resultMap;
    }
}
