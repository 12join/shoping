app.controller("itemController",function($scope,$http,favoriteService,loginService){
	
	$scope.specificationItems={};//存储用户选择的规格
	
	//数量加减
	$scope.addNum=function(x){
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}		
	}
	
	//用户选择规格
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key]=value;		
		searchSku();//查询SKU
	}
	
	//判断某规格是否被选中
	$scope.isSelected=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}	
	}
	
	$scope.sku={};//当前选择的SKU
	
	//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}
	
	//匹配两个对象是否相等
	matchObject=function(map1,map2){
		
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}			
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}			
		}		
		return true;
		
	}
	
	//根据规格查询sku
	searchSku=function(){
		
		for(var i=0;i<skuList.length;i++){
			 if(matchObject( skuList[i].spec ,$scope.specificationItems)){
				 $scope.sku=skuList[i];
				 return ;
			 }			
		}
		$scope.sku={id:0,title:'-----',price:0};
	}
	
	//添加商品到购物车
	$scope.addToCart=function(){
		//alert('SKUID:'+$scope.sku.id );		
		
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
				+$scope.sku.id+'&num='+$scope.num ,{'withCredentials':true} ).success(
					function(response){
						if(response.success){
							location.href='http://localhost:9107/cart.html';						
						}else{
							alert(response.message);
						}					
					}						
				);	
		
	}

	$scope.favorite=function () {
        favoriteService.save($scope.sku.id).success(function (data) {
			alert(data.message);
            $scope.isFav=true;
        });
    }

    //获取用户登陆信息
    $scope.user={name:"",isLogin:false};
    $scope.showName=function(){

        loginService.showName().success(

            function(data){

                $scope.user.name=data.loginName;

                $scope.user.isLogin=data.isLogin;

            }
        );
    }
    //判断用户是否收藏
    $scope.isFavor=function () {
        favoriteService.isFavor($scope.sku.id).success(function (data) {
            $scope.isFav= data.success;
        });
    }
	
	
});