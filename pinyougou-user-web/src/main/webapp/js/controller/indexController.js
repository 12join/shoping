//首页控制器
app.controller('indexController',function($scope,loginService,orderService,userService){
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

    $scope.timeString={};
    //获取用户订单信息
    $scope.findUserOrder=function(){
        orderService.findUserOrder().success(
            function(response){
                $scope.orderList=response;
                $scope.getImage();
                //获取用户所有订单状态数组
                getStatus();







//			//前端倒计时
//			for(var i =0;i<$scope.orderList.length;i++){
//
//				if($scope.status[$scope.orderList[i].order.orderId]=='4'||$scope.status[$scope.orderList[i].order.orderId]=='5'){
//					var id=$scope.status[$scope.orderList[i].order.orderId]
//					var day15= 60*60*24*15;
//
//					allSecond=Math.floor( day15-(new Date().getTime()-new Date($scope.orderList[i].order.updateTime).getTime())/1000);
//					var time = $interval(function(){
//						if(allSecond>0){
//							allSecond = allSecond-1;
//
//							$scope.timeString[id] =convert(allSecond);
//						}
//						else{
//							$interval.cancel(time)
//						}
//
//					},1000)
//
//				}
//				if($scope.status[$scope.orderList[i].order.orderId]=='1'){
//					var id=$scope.status[$scope.orderList[i].order.orderId]
//					//5小时不支付自动取消订单
//					var hour5= 60*60*5;
//
//					allSecond1=Math.floor( hour5-(new Date().getTime()-new Date($scope.orderList[i].order.updateTime).getTime())/1000);
//					var time = $interval(function(){
//						if(allSecond1>0){
//							allSecond = allSecond1-1;
//							//alert($scope.payTimeString)
//							//$scope.timeString[id] =convert(allSecond1);
//						}
//						else{
//							$interval.cancel(time)
//						}
//
//					},1000)
//
//				}

//			}



            }
        )
    }

   /* $scope.status={};//支付状态数组
    getStatus=function(){
        //获取支付状态
        for(var i=0;i<$scope.orderList.length;i++){

        }
    }*/



    //发送订单消息
    $scope.sendMessage= function (order) {
       /* if (ordersItem. == null || ordersItem.receiverMobile == "") {
            alert("请填写手机号码");
            return;
        }*/
        userService.sendMessage(order).success(
            function (response) {
                alert(response.message);
            }
        );
    }
});