//服务层
app.service('orderService',function($http){
    //获取用户所有状态的信息
    this.findUserOrder=function(){
        return $http.get('../order/userOrder.do');
    }

});