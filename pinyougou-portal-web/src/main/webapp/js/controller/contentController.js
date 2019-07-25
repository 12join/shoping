app.controller("contentController",function($scope,contentService,loginService){
	$scope.contentList = [];
	// 根据分类ID查询广告的方法:
	$scope.findByCategoryId = function(categoryId){
		contentService.findByCategoryId(categoryId).success(function(response){
			$scope.contentList[categoryId] = response;
		});
	}
	
	//搜索  （传递参数）
	$scope.search=function(){
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}

    //获取用户登陆信息
    $scope.user={name:"",isLogin:false};
    $scope.showName=function(){
        loginService.showName().success(
            function(data){
                $scope.url=window.location.host+window.location.search;
                $scope.user.name=data.loginName;
                $scope.user.isLogin=data.isLogin;
            });
    }


	
});