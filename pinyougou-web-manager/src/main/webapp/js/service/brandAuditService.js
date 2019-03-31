//服务层
app.service('brandAuditService',function($http){
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../brand/searchAudit.do?pageNum='+page+"&pageSize="+rows, searchEntity);
	}    
	
	this.updateStatus = function(ids,status){
		return $http.get('../brand/updateStatus.do?ids='+ids+"&status="+status);
	}
});
