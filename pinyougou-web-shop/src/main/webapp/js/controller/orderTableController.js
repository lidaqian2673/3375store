 //控制层 
app.controller('orderTableController' ,function($scope,$controller,orderTableService){
	
	$controller('baseController',{$scope:$scope});//继承

    //批量删除
    $scope.findorderTable=function(){
        orderTableService.findorderTable().success(
            function(response){

                    // $scope.reloadList();//刷新列表
                    $scope.orderTableList = response;

            }
        );
    }

    $scope.initaaaa = function () {
        alert(1);
    };
















});	
