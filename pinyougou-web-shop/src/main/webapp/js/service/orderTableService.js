//服务层
app.service('orderTableService',function($http){
    //删除
    this.findorderTable=function(){
        return $http.get('../orderTable/findorderTable.do');
}

});
