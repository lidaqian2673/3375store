app.service("contentService",function($http){
	this.findByCategoryId = function(categoryId){
		return $http.get("content/findByCategoryId.do?categoryId="+categoryId);
	}
    this.findAllCategory = function(){
        return $http.get("content/findAllCategory.do");
    }
});