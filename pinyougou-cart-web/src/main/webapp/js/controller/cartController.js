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
	};
	

	
	//获取当前用户的地址列表
	$scope.findAddressList=function(){
		cartService.findAddressList().success(
			function(response){
				$scope.addressList=response;
				for(var i=0;i<$scope.addressList.length;i++){
					if($scope.addressList[i].address.isDefault=='1'){
						$scope.id=$scope.addressList[i].address.id;
						break;
					}					
				}
				
			}
		);		
	}
	
	//选择地址
	$scope.selectAddress=function(id){
		$scope.id=id;
	}
	
	//判断某地址对象是不是当前选择的地址
	$scope.isSelectAddress=function(id){
		if(id==$scope.id){
			return true;
		}else{
			return false;
		}		
	};


    $scope.address={};
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.address.userId!=null){//如果有ID
            serviceObject=cartService.update($scope.address); //修改
        }else{
            serviceObject=cartService.add($scope.address);//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    alert(response.message);
                    $scope.findAddressList();//重新加载
                    $scope.address={};
                }else{
                    alert(response.message);
                }
            }
        );
    };

    $scope.ids=[];
    //删除
    $scope.dele=function(id){
        if(!confirm("你确定要删除吗")){
            return;
        }
        $scope.ids.push(id);
        cartService.dele($scope.ids).success(
            function(response){
                if(response.success){
                    $scope.findAddressList();//重新加载
                }else{
                    alert(response.message);
                }
                $scope.ids=[];
            }
        );
    };

    //查找一个
    $scope.findOne=function(address){
        $scope.address=JSON.parse(JSON.stringify(address));


        $scope.$watch('address.provinceId',function(newValue,oldValue){
            if(newValue != undefined){
                cartService.findC(newValue).success(
                    function(response){
                        $scope.areasList={};
                        $scope.cityList=response;

                    }
                );
            }
        });

        $scope.$watch('address.cityId',function(newValue,oldValue){
            if(newValue != undefined){
                cartService.findA(newValue).success(
                    function(response){
                        $scope.areasList=response;
                    }
                );
            }
        })


    };

    $scope.findP=function () {
        cartService.findP().success(
            function(response){
                $scope.provinceList=response;
            }
        );
    };

	
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