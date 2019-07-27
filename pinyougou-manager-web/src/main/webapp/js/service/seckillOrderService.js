app.service("seckillOrderService",function($http){

    //读取列表数据绑定到表单中
    this.findAll=function(){
        return $http.get('../seckillOrder/findAll.do');
    };
    //分页
    this.findPage=function(page,rows){
        return $http.get('../seckillOrder/findPage.do?page='+page+'&rows='+rows);
    };
    //查询实体
    this.findOne=function(id){
        return $http.get('../seckillOrder/findOne.do?id='+id);
    };
    //删除
    this.dele=function(ids){
        return $http.get('../seckillOrder/delete.do?ids='+ids);
    };
    //搜索
    this.search=function(page,rows,searchEntity){
        return $http.post('../seckillOrder/search.do?page='+page+"&rows="+rows, searchEntity);
    };

    this.createExcel=function(list){
        return $http.post('../seckillOrder/createExcel.do',list);
    };

});