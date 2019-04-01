 //控制层 
app.controller('specificationAuditController' ,function($scope,$controller  ,specificationAuditService){
	
	$controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
	$scope.findAll=function(){
		specificationService.findAll().success(
			function(response){
				$scope.list=response;
			}
		);
	}
    // 显示状态
    $scope.status = ["未审核","审核通过","审核未通过","关闭"];
    //审核规格,修改规格的状态
    $scope.updateStatus = function(status){
        specificationAuditService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];//清空选中的id
            }else{
                alert(response.message);
            }
        });
    }

	//分页
	$scope.findPage=function(page,rows){
		specificationService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

	//查询实体
	$scope.findOne=function(id){
        specificationAuditService.findOne(id).success(
			function(response){
				$scope.entity= response;
			}
		);
	}

	//保存
	$scope.save=function(){
		var serviceObject;//服务层对象
		if($scope.entity.specification.id!=null){//如果有ID
			serviceObject=specificationService.update( $scope.entity ); //修改
		}else{
			serviceObject=specificationService.add( $scope.entity  );//增加
		}
		serviceObject.success(
			function(response){
				if(response.flag){
					//重新查询
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}
		);
	}


	//批量删除
	$scope.dele=function(){
		//获取选中的复选框
		specificationService.dele( $scope.selectIds ).success(
			function(response){
				if(response.flag){
					$scope.reloadList();//刷新列表
					$scope.selectIds = [];
				}
			}
		);
	}

	$scope.searchEntity={};//定义搜索对象 
	
	//搜索,保留,是查询所有未审核的规格及规格选项
	$scope.search=function(page,rows){
        specificationAuditService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	

	$scope.addTableRow = function(){
		$scope.entity.specificationOptionList.push({});
	}

	$scope.deleteTableRow = function(index){
		$scope.entity.specificationOptionList.splice(index,1);
	}

});	
