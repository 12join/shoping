//首页控制器
app.controller('indexController',function($scope,loginService,orderService,userService){
app.controller('indexController',function($scope,$location,$interval,loginService,orderService,payService){
    //获取用户登陆信息
    $scope.user={name:"",isLogin:false};
    $scope.showName=function(){

        loginService.showName().success(

            function(data){

                $scope.user.name=data.loginName;

                $scope.user.isLogin=data.isLogin;

            }
        );
    };

    $scope.num=1;
    //获取用户订单信息
    $scope.findUserOrder=function(){
        $scope.num= parseInt($scope.num);
        orderService.findUserOrder($scope.num).success(
            function(response){
                $scope.orderList=response.rows;
                $scope.total=response.total;
                $scope.totalPages=Math.ceil($scope.total/5);
                $scope.buildPageLabel();
                //$scope.getImage();
                //获取用户所有订单状态数组
                getStatus();
                $scope.getPayLogList();
                $('html, body').animate({scrollTop:0}, 'slow');
            }
        )
    };


     	//构建页码
	$scope.buildPageLabel=function(){
		$scope.pageList=[];
		var maxPage=$scope.totalPages;
		var firstPage=1;
		var lastPage=maxPage;
		$scope.firstDot=true;//前面有点
		$scope.lastDot=true;//后边有点
		if(maxPage>5){
			if($scope.num<=3){
				lastPage=5;
				$scope.firstDot=false;
			}else if($scope.num>=maxPage-2){
				firstPage=maxPage-4;
				$scope.lastDot=false;
			}else{
				firstPage=$scope.num-2;
				lastPage=$scope.num+2;
			}
		}else{
			$scope.firstDot=false;
			$scope.lastDot=false;
		}
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageList.push(i);
		}

	};
	//根据页码查询
	$scope.queryByPage=function(pageNum,status){
		if(pageNum<1||pageNum>$scope.totalPages){
			return;
		}
		$scope.num=pageNum;
		if(status==null||""==status){
            $scope.findUserOrder();
        }else{
            $scope.orderStatus(status);
        }



	};

	//跳转页初始化
	$scope.num2='';

	//判断是否是第一页
	$scope.isFirst=function(){
		if($scope.num==1){
			return true;
		}else{
			return false;
		}
	};
	//判断是否是最后一页
	$scope.isLast=function(){
		if($scope.num==$scope.totalPages){
			return true;
		}else{
			return false;
		}
	};






    $scope.status={};//支付状态数组

    getStatus=function(){//获取支付状态方法

        for(var i = 0;i <$scope.orderList.length;i++){
            //把每个订单的支付状态放在一个数组,订单ID为键
            //alert($scope.orderList[i].order.orderId)
            $scope.status[$scope.orderList[i].order.orderId] = $scope.orderList[i].order.status;
        }
    }

    //状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价


    //根据订单状态查询订单列表
    $scope.orderStatus=function(status){
        orderService.orderPayStatus(status,$scope.num).success(function(response){
            //$scope.getImage();
            $scope.orderList=response.rows;
            $scope.total=response.total;
            $scope.totalPages=Math.ceil($scope.total/10);
            $scope.buildPageLabel();
            getStatus();
            $scope.getPayLogList();
            $('html, body').animate({scrollTop:0}, 'slow');
        })
    };


    //取消订单
    $scope.cancelOrder=function(orderId){
        for(var i =0;i< $scope.payLogList.length;i++){
            //alert($scope.payLogList[i].outTradeNo)
            if($scope.payLogList[i].orderList.indexOf(orderId)!=-1){

                alert($scope.payLogList[i].outTradeNo)
                $scope.logId=$scope.payLogList[i].outTradeNo;
            }
        }

        orderService.cancelOrder($scope.logId).success(function(response){
            if(response.success){
                alert(response.message);
                location.reload();
            }else{
                alert(response.message);
            }
        })
    };

    //提醒发货
    $scope.remindSend=function(orderId){
        orderService.remindSend(orderId).success(function(response){
            if(response.success){
                alert("提醒发货成功")
                location.reload();
            }else{
                alert("状态异常，请稍后重试");
            }
        })
    }

    //确认收货
    $scope.confirmOrder=function(orderId){
        orderService.confirmOrder(orderId).success(function(response){
            if(response.success){
                alert(response.message);
                location.reload();
            }
        })
    };

    //延长收货
    $scope.delayReceive=function(orderId){
        orderService.delayReceive(orderId).success(function (response) {
            if(response.success){
                alert("延长收货成功");
            }else{
                alert("状态异常");
            }
        })
    }


    //倒计时格式转换
    convert=function(t){
        day = Math.floor((t/(60*60*24)));
        hour = Math.floor(((t-day*60*60*24)/(60*60)));
        minute=Math.floor((t-day*60*60*24-hour*60*60)/60);
        second=Math.floor(t-day*60*60*24-hour*60*60-minute*60);
        var timeString ="";

        if(day>0){
            timeString=day+"天";
            if(hour>0){
                timeString=day+"天"+hour+"小时";
            }
        }

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
        return timeString+minute+"分钟"+second+"秒"
    }


    //删除订单
    $scope.deleOrder=function(orderId){
        orderService.deleOrder(orderId).success(function(response){
            if(response.success){
                alert(response.message);
                location.reload();
            }else{
                alert(response.message);
            }
        })
    };


    //获取订单详情，包括从订单页面跳转和从支付成功页面跳转
    $scope.getAllDetail=function(){
        var orderId = $location.search()['orderId'];
        var payLogId = $location.search()['payLogId'];
        if(orderId==""||orderId==null){
            $scope.getDetail(payLogId);
        }
        if(payLogId==""||payLogId==null){
            $scope.orderDetail();
        }
    };
    //从订单页面跳转
    $scope.orderDetail=function(){
        $scope.orderId3= $location.search()['orderId'];
        orderService.orderDetail($scope.orderId3).success(function(response){
            $scope.order=response;
            if($scope.order.order.status=='1'){
                var day15Sec= 60*60;
                allSecond2=Math.floor( day15Sec-(new Date().getTime()-new Date($scope.order.order.createTime).getTime())/1000);
                var time2 = $interval(function(){
                    if(allSecond2>0){
                        allSecond2 = allSecond2-1;
                        $scope.receTimeString =convert(allSecond2);
                    }
                    else{
                        $interval.cancel(time2)
                    }
                },1000)
            }
            if($scope.order.order.consignTime!=null&&$scope.order.order.status!='5'){
                var day15Sec= 60*60*24*15;
                allSecond2=Math.floor( day15Sec-(new Date().getTime()-new Date($scope.order.order.consignTime).getTime())/1000);
                var time2 = $interval(function(){
                    if(allSecond2>0){
                        allSecond2 = allSecond2-1;
                        $scope.receTimeString =convert(allSecond2);
                    }
                    else{
                        $interval.cancel(time2)
                    }
                },1000)
            }
        })
    };

    //通过payLogId获取订单号，再查询对应的订单详情，从支付成功页面跳转
    $scope.getDetail=function(payLogId){

        orderService.getOrderIdByPayLog(payLogId).success(function(response){
            //前端传递的是字符串数字，将双引号给截取掉在传递
            orderService.orderDetail(response.substring(1,response.length-1)).success(function(response1){

                $scope.order=response1;
                if($scope.order.order.consignTime!=null&&$scope.order.order.status!='5'){

                    var day15Sec= 60*60*24*15;

                    allSecond2=Math.floor( day15Sec-(new Date().getTime()-new Date($scope.order.order.consignTime).getTime())/1000);

                    var time2 = $interval(function(){
                        if(allSecond2>0){
                            allSecond2 = allSecond2-1;

                            $scope.receTimeString =convert(allSecond2);
                        }
                        else{
                            $interval.cancel(time2)
                        }

                    },1000)

                }

            })
        })
    };


    //订单详情页状态数组
    $scope.currentStatus=['','未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价 '];

    $scope.parseToNum=function(status){
        return parseInt(status);
    };




    $scope.payLogList=[];
    //查询用户所有的订单日志，用于支付时查询支付订单号,在我的订单页面和代付款页面的查询订单方法中调用
    $scope.getPayLogList=function(){
        orderService.getPayLogList().success(function(response){
            $scope.payLogList=response;
        })

    };


    //付款相关

    //跳转支付页面（这里需要传递支付订单号，等下再做）
    $scope.toPay=function(type){

       var logList =  JSON.stringify($scope.payLogList)

        alert(logList)
        if(type=='1'){
            location.href="pay.html#?type=1&currentOrderId="+$scope.currentOrderId+"&payLogList="+logList;
        }else if(type=='3'){
            location.href="pay.html#?type=3&currentOrderId="+$scope.currentOrderId+"&payLogList="+logList;
        }
        else{
            location.href="paysuccess.html"
        }
    };


    $scope.logId=""

    $scope.createNative=function(){

        $scope.payLogList=  $location.search()['payLogList'];
        $scope.payLogList = JSON.parse($scope.payLogList);
        console.info( $scope.payLogList)

        var type =  $location.search()['type'];

        var currentOrderId = $location.search()['currentOrderId'];


        for(var i =0;i< $scope.payLogList.length;i++){
            //alert($scope.payLogList[i].outTradeNo)
            if($scope.payLogList[i].orderList.indexOf(currentOrderId)!=-1){
                alert($scope.payLogList[i].orderList.indexOf(currentOrderId))
                $scope.logId=$scope.payLogList[i].outTradeNo;
            }
        }

        if(type=='1'){
            $scope.name='微信';
        }if(type=='3'){
            $scope.name='支付宝';

        }

        alert($scope.logId)
        payService.createNative(type,$scope.logId).success(function(response){
            $scope.orderId=response.out_trade_no;
            $scope.money=(response.total_fee/100).toFixed(2);
            var qr = new QRious({

                element:document.getElementById('qrious'),
                size:250,
                value:response.code_url,
                level:'H'
            });

            queryStatus(type);
        })

    };



    //支付状态查询
    queryStatus=function(type){
        payService.queryStatus($scope.orderId,type).success(function(response){
            if(response.success){
                location.href="paysuccess.html#?money="+$scope.money+'&type='+type+'&orderId='+$scope.orderId;
            }else{
                if(response.message=="二维码超时"){
                    $scope.createNative();
                }else{
                    location.href="payfail.html";
                }
            }
        })
    }

    //选择付款方式
    $scope.selectPayment=function(type){
        $scope.selectType=type;

    };


    //支付成功页面显示支付金额
    $scope.showMoney=function(){
        return	$location.search()['money'];
    }

    //支付成功页面显示支付方式
    $scope.getType=function(){

        var type1 =  $location.search()['type'];
        $scope.payLog =  $location.search()['orderId'];
        //创建方法，通过支付日志ID获取到对应订单的ID，然后将所有的订单详情展示到订单详情页

        if(type1=='1'){
            $scope.payType='微信';
        }if(type1=='3'){
            $scope.payType='支付宝';

        }

    };


    //付款相关结束


    //点击立即支付时绑定此订单的订单ID到一个公共变量上用来获得支付订单号
    $scope.getOrderId=function(orderId){

        $scope.currentOrderId=orderId;
        alert($scope.currentOrderId);
    }



});