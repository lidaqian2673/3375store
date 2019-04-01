//服务层
app.service('specificationAuditService',function($http){

    //修改规格状态
    this.updateStatus = function(ids,status){
        return $http.get('../specification/updateStatus.do?ids='+ids+"&status="+status);
    }
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../specification/findAll.do');
	}
	//分页
	this.findPage=function(page,rows){
		return $http.get('../specification/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../specification/findOne.do?id='+id);
	}
	//增加
	this.add=function(entity){
		return  $http.post('../specification/add.do',entity );
	}
	//修改
	this.update=function(entity){
		return  $http.post('../specification/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../specification/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../specification/findNotAuditSpec.do?page='+page+"&rows="+rows, searchEntity);
	}  

	this.selectOptionList=function(){
		return $http.get("../specification/selectOptionList.do");
	}
});
