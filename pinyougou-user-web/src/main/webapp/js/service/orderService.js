//服务层
app.service('orderService',function($http){
    //获取用户所有状态的信息
    this.findUserOrder=function(){
        return $http.get('../order/userOrder.do');
    }

    //获取所有的支付日志
    this.getPayLogList=function(){
        return $http.get('../order/getPayLogList.do');
    }
    //根据订单状态查询订单
    this.orderPayStatus=function(status){
        return $http.get('../order/orderStatus.do?status='+status);
    }

    //取消订单
    this.cancelOrder=function(){
        return $http.get('../order/cancelOrder.do');
    }
    //确认收货
    this.confirmOrder=function(orderId){
        return $http.get('../order/confirmOrder.do?orderId='+orderId);
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

    //通过orderId获取对应订单详情信息
    this.findOne=function(orderId){
        return $http.get('../order/findOne.do?orderId='+orderId);
    }

});