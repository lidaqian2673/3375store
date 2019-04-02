 //控制层 
app.controller('itemCatAuditController' ,function($scope,$controller,itemCatAuditService){
	
	$controller('baseController',{$scope:$scope});//继承

	//搜索
	$scope.findAuditList=function(){
        itemCatAuditService.findAuditList().success(
			function(response){
				$scope.list=response;
			}			
		);
	}


    // 审核的方法:
    $scope.updateStatus = function(status){
        itemCatAuditService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                location.reload();//刷新列表
                $scope.selectIds = [];
            }else{
                alert(response.message);
            }
        });
    }


	// 定义一个变量记录当前是第几级分类
	$scope.grade = 1;
	
	$scope.setGrade = function(value){
		$scope.grade = value;
	}
	
	$scope.selectList = function(p_entity){
		
		if($scope.grade == 1){
			$scope.entity_1 = null;
			$scope.entity_2 = null;
		}
		if($scope.grade == 2){
			$scope.entity_1 = p_entity;
			$scope.entity_2 = null;
		}
		if($scope.grade == 3){
			$scope.entity_2 = p_entity;
		}
		
		$scope.findByParentId(p_entity.id);
	}

});	
