app.service("orderService",function($http){

    //读取列表数据绑定到表单中
    this.findAll=function(){
        return $http.get('../order/findAll.do');
    };
    //分页
    this.findPage=function(page,rows){
        return $http.get('../order/findPage.do?page='+page+'&rows='+rows);
    };
    //查询实体
    this.findOne=function(id){
        return $http.get('../order/findOne.do?id='+id);
    };
    //删除
    this.dele=function(ids){
        return $http.get('../order/delete.do?ids='+ids);
    };
    //搜索
    this.search=function(page,rows,searchEntity){
        return $http.post('../order/search.do?page='+page+"&rows="+rows, searchEntity);
    };

    this.createExcel=function(list){
        return $http.post('../order/createExcel.do',list);
    };

    this.deleteExcel=function(name){
        return $http.get('../order/deleteExcel.do?name='+name);
    };

    this.updateStatus=function(order){
        return $http.post('../order/update.do',order);
    };

});