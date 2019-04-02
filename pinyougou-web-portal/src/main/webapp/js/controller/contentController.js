app.controller("contentController",function($scope,contentService,itemCatService){

	//数组
	$scope.contentList = [];


	// 根据分类ID查询广告的方法:
	//入参：categoryId 分类Id  1 轮播图 2 今天  3 活动。。。
	$scope.findByCategoryId = function(categoryId){
		contentService.findByCategoryId(categoryId).success(function(response){
			$scope.contentList[categoryId] = response;//List<Content>
		});
	}


	//搜索  （传递参数）
	$scope.search=function(){
		location.href="http://localhost:9003/search.html#?keywords="+$scope.keywords;
	}


	//获取商品分类信息
    $scope.findItemCatList=function(){
        itemCatService.findItemCatList().success(function (response){
            $scope.MapList = response;
        });
	}

    $scope.search1=function(key){
        location.href="http://localhost:9003/search.html#?keywords="+key;
    }

});