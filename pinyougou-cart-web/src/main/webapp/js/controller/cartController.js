//购物车控制层
app.controller('cartController',function($scope,cartService,loginService){
	//查询购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				$scope.totalValue= cartService.sum($scope.cartList);
			}
		);
	}
	
	//数量加减
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(
			function(response){
				if(response.success){//如果成功
					$scope.findCartList();//刷新列表
				}else{
					alert(response.message);
				}				
			}		
		);		
	}
	

	
	//获取当前用户的地址列表
	$scope.findAddressList=function(){
		cartService.findAddressList().success(
			function(response){
				$scope.addressList=response;
				for(var i=0;i<$scope.addressList.length;i++){
					if($scope.addressList[i].isDefault=='1'){
						$scope.address=$scope.addressList[i];
						break;
					}					
				}
				
			}
		);		
	}
	
	//选择地址
	$scope.selectAddress=function(address){
		$scope.address=address;		
	}
	
	//判断某地址对象是不是当前选择的地址
	$scope.isSeletedAddress=function(address){
		if(address==$scope.address){
			return true;
		}else{
			return false;
		}		
	}
	
	$scope.order={paymentType:'1'};//订单对象
	
	//选择支付类型
	$scope.selectPayType=function(type){
		$scope.order.paymentType=type;
	}
	
	//保存订单
	$scope.submitOrder=function(){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人
		
		cartService.submitOrder( $scope.order ).success(
			function(response){
				//根据支付方式选择不同的生成二维码方法，微信或支付宝
				if(response.success){
					//得到后端传递的payLogId
					var payLogId = response.message;
					//微信支付
                    if($scope.order.paymentType=='1'){
                        location.href="pay.html#?type=1&payLogId="+payLogId;
                    }else if($scope.order.paymentType=='3'){//支付宝支付
                        location.href="pay.html#?type=3&payLogId="+payLogId;
                    }
                    else{
                        location.href="paysuccess.html"
                    }

				}else{
					alert(response.message);	//也可以跳转到提示页面
				}
				
			}
		);		
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
	
});