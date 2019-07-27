//服务层
app.service('orderService',function($http){
    //获取用户所有状态的信息
    this.findUserOrder=function(page){
        return $http.get('../order/userOrder.do?page='+page);
    }

    //获取所有的支付日志
    this.getPayLogList=function(){
        return $http.get('../order/getPayLogList.do');
    }
    //根据订单状态查询订单
    this.orderPayStatus=function(status,page){
        return $http.get('../order/orderStatus.do?status='+status+'&page='+page);
    }

    //取消订单
    this.cancelOrder=function(payLogId){
        return $http.get('../order/cancelOrder.do?payLogId='+payLogId);
    }

    //提醒发货

    this.remindSend=function(orderId){
        return $http.get('../order/remindSend.do?orderId='+orderId);
    }
    //确认收货
    this.confirmOrder=function(orderId){
        return $http.get('../order/confirmOrder.do?orderId='+orderId);
    }

    //延长收货
    this.delayReceive=function(orderId){
        return $http.get('../order/delayReceive.do?orderId='+orderId);
    }

    //删除订单
    this.deleOrder = function(orderId){
        return $http.get('../order/deleOrder.do?orderId='+orderId);
    }


    //获取订单详情页所需要的信息
    this.orderDetail=function(orderId){
        return $http.get('../order/orderDetail.do?orderId='+orderId);
    }

    //通过payLogI获取对应订单详情信息
    this.getOrderIdByPayLog=function(payLogId){
        return $http.get('../order/getOrderId.do?payLogId='+payLogId);
    }



});