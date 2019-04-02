//控制层 
app.controller('typeTemplateAuditController' ,function($scope,$controller,brandService ,specificationService  ,typeTemplateAuditService){
	
	$controller('baseController',{$scope:$scope});//继承


	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){
        typeTemplateAuditService.searchAudit(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


    // 显示状态
    $scope.status = ["未审核","审核通过","审核未通过","关闭"];

	// 审核的方法:
    $scope.updateStatus = function(status){
        typeTemplateAuditService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];
            }else{
                alert(response.message);
            }
        });
    }


    $scope.brandList={data:[]};
    // 查询关联的品牌信息:
    $scope.findBrandList = function(){
        brandService.selectOptionList().success(function(response){//返回值List<Map>
            $scope.brandList = {data:response};
        });
    }

    $scope.specList={data:[]}
	// 查询关联的品牌信息:
	$scope.findSpecList = function(){
		specificationService.selectOptionList().success(function(response){
			$scope.specList = {data:response};
		});
	}

});	
