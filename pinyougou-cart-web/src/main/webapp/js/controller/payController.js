app.controller('payController' ,function($scope ,$location,payService,loginService){
	
	
	$scope.createNative=function(){
		//获取支付方式
		var type =  $location.search()['type'];

		var payLogId=$location.search()['payLogId'];
		payService.createNative(type,payLogId).success(
			function(response){
                showTypeName(type);
				//显示订单号和金额
				$scope.money= (response.total_fee/100).toFixed(2);
				$scope.out_trade_no=response.out_trade_no;
				
				//生成二维码
				 var qr=new QRious({
					    element:document.getElementById('qrious'),
						size:250,
                        level:'L',
						value:response.code_url
			     });
				 
				 queryPayStatus(type);//调用查询
				
			}	
		);	
	}
	
	//调用查询
	queryPayStatus=function(type){
		payService.queryPayStatus($scope.out_trade_no,type).success(
			function(response){
				if(response.success){
					location.href="paysuccess.html#?money="+$scope.money+'&type='+type;//type在支付成功页面中展示付款方式
				}else{
					if(response.message=='二维码超时'){
						$scope.createNative();//重新生成二维码
					}else{
						location.href="payfail.html";
					}
				}				
			}		
		);		
	}
	
	//获取金额
	$scope.getMoney=function(){
		return $location.search()['money'];
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

    //展示根据type在支付页面中展示支付方式
	showTypeName=function(type){
    	if(type==1){
    		$scope.typeName='微信';
		}
		if(type==3){
    		$scope.typeName='支付宝';
		}
	}

    //支付成功页面显示支付方式
    $scope.getType=function(){

        var type1 =  $location.search()['type'];
        if(type1==1){
            $scope.payType='微信';
        }if(type1==3){
            $scope.payType='支付宝';

        }

    }


	
});