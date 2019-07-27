//服务层
app.service('addressService',function($http){

    //获取地址列表
    this.findAddressList=function(){
        return $http.get('address/findListByLoginUser.do');
    }


    //增加地址
    this.add=function(address){
        return  $http.post('address/add.do',address);
    }

    //修改地址
    this.update=function(address){
        return  $http.post('address/update.do',address);
    }


    //删除地址
    this.dele=function(ids){
        return $http.get('address/delete.do?ids='+ids);
    }

    //查询省
    this.findP=function(){
        return $http.get('address/findProvince.do');
    }

    //查询市
    this.findC=function(id){
        return $http.get('address/findCity.do?id='+id);
    }

    //查询市
    this.findA=function(id){
        return $http.get('address/findAreas.do?id='+id);
    }

});