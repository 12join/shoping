//服务层
app.service('addressService',function($http){
    //读取列表数据绑定到表单中
    this.showName=function(){
        return $http.get('../address/findList.do');
    }

});