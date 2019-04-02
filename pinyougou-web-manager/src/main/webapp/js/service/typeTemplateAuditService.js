//服务层
app.service('typeTemplateAuditService',function($http){

    //修改审核状态
    this.updateStatus = function(ids,status){
        return $http.get('../typeTemplate/updateStatus.do?ids='+ids+"&status="+status);
    }
    //搜索
    this.searchAudit=function(page,rows,searchEntity){
        return $http.post('../typeTemplate/searchAudit.do?page='+page+"&rows="+rows, searchEntity);
    }
});
