//控制层
app.controller('seckillGoodsController' ,function($scope,seckillGoodsService,$location,$interval,loginService){
	//读取列表数据绑定到表单中
	$scope.findAll=function(){
		seckillGoodsService.findAll().success(
			function(response){
				$scope.list=response;
			} 
		);
	}
	
	$scope.findOne=function(){ 
		seckillGoodsService.findOne($location.search()["id"]).success(
			function(response){
				$scope.entity=response;
				allSecond=Math.floor((new Date($scope.entity.endTime).getTime()-new Date().getTime())/1000);
			}
		);
	}
	var allSecond;
	time=$interval(function(){
		allSecond--;
		$scope.timeString=convertTimeString(allSecond);//转换时间字符串
		if(allSecond<=0){
			$interval.cancel(time);
			alert("秒杀服务已结束");
			$location.href="seckill-index.html";
		}
	},1000);
	
	
	//转换秒为 天小时分钟秒格式 XXX 天 10:22:33
	convertTimeString=function(allSecond){
		var days=Math.floor( allSecond/(60*60*24));//天数
		var hours=Math.floor( (allSecond-days*60*60*24)/(60*60) );//小数数
		var minutes=Math.floor((allSecond -days*60*60*24 - hours*60*60)/60 );//分钟数
		var seconds=allSecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
		var timeString="";
		if(days>0){
			timeString=days+"天 ";
		}
		if(hours<10){
			timeString+="0"+hours+":";
		}else{
			timeString+=hours+":";
		}
		if(minutes<10){
			timeString+="0"+minutes+":";
		}else{
			timeString+=minutes+":";
		}
		if(seconds<10){
			timeString+="0"+seconds;
		}else{
			timeString+=seconds;
		}
		return timeString;
	}
	
	$scope.submitOrder=function(){
		seckillGoodsService.submitOrder($scope.entity.id).success(
			function(response){
				if(response.success){
					alert("下单成功，请在 5分钟内完成支付");
					location.href="pay.html";
				}else{
				    if(response.message="用户未登录"){
                        alert(response.message);
                        location.href="login.html";
                    }else {
                        alert(response.message);
                    }
				}
			}
		);
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