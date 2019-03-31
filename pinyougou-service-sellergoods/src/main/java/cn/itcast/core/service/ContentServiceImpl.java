package cn.itcast.core.service;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContentServiceImpl implements ContentService {
	
	@Autowired
	private ContentDao contentDao;

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(Content content) {

		contentDao.insertSelective(content);
	}


	//广告管理 之修改 提交
	@Override
	public void edit(Content content) {

		//1:要清除之前广告分类ID下的所有广告 轮播图
		Content c = contentDao.selectByPrimaryKey(content.getId());//之前的广告
		//判断是否修改了广告分类ID?
		if(!c.getCategoryId().equals(content.getCategoryId())){
			//之前的广告分类ID下的所有广告结果集清除
			redisTemplate.boundHashOps("content").delete(c.getCategoryId());
		}
		//2:只清除之后 此广告分类ID下的所有广告  今日推荐
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());

		//3：直接修改了Mysql数据库
		contentDao.updateByPrimaryKeySelective(content);
	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}

	@Autowired
	private RedisTemplate redisTemplate;

	//根据广告分类ID 查询此分类下所有广告集合
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
		//1:从缓存中查询轮播图位置上的广告结果集
		//Map content = new HashMap();
		// 广告分类 1:K：轮播图  V：广告结果集 长度5条中第一条：牙刷：修改Mysql数据库 ： 缓存是如何清除的呢？

		// 广告分类 2:今日推荐  V：广告结果集
		// 广告分类 3:活动专区  V：广告结果集
		List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
		if(null == contentList || contentList.size() == 0){
			//2:没有  去Mysql数据库查询
			ContentQuery contentQuery = new ContentQuery();
			contentQuery.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
			//排序
			contentQuery.setOrderByClause("sort_order desc");
			contentList = contentDao.selectByExample(contentQuery);
		    //3:查询出来之后放到缓存中一份
			redisTemplate.boundHashOps("content").put(categoryId,contentList);
			//存活时间
			redisTemplate.boundHashOps("content").expire(8, TimeUnit.HOURS);

		}
		//返回
        return contentList;
    }

}
