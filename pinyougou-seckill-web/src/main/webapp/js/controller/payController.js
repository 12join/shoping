app.controller('payController' ,function($scope,$location,payService,loginService){
	// 本地生成二维码
	$scope.createNative=function(){
		payService.createNative().success(
			function(response){
				$scope.money=(response.total_fee/100).toFixed(2);// 金额
				$scope.out_trade_no=response.out_trade_no;// 订单号
				// 二维码
				var qr = new QRious({
					element:document.getElementById('qrious'),
					size:250,
					level:'L',
					value:response.code_url
				});
				queryPayStatus(response.out_trade_no);//查询支付状态 
			}
		);
	} 
	
	
	// 查询支付状态
	queryPayStatus=function(out_trade_no){
		payService.queryPayStatus(out_trade_no).success(
			function(response){
				if(response.success){
					location.href="paysuccess.html#?money="+$scope.money;
				}else{ 
					if(response.message=='支付超时'){
						location.href="payTimeOut.html";
					}else{
						location.href="payfail.html";
					}
				} 
			}
		);
	}
	
	$scope.getMoney=function(){
		return $location.search()['money'];
	}

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