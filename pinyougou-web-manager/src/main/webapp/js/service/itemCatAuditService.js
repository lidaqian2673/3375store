//服务层
app.service('itemCatAuditService',function($http){

	//搜索
	this.findAuditList=function(){
		return $http.get('../itemCat/findAuditList.do');
	}
	//修改审核状态
    this.updateStatus = function(ids,status){
        return $http.get('../itemCat/updateStatus.do?ids='+ids+"&status="+status);
    }
});
