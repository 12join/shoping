app.controller("seckillOrderController", function ($controller,$scope, seckillOrderService) {

    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
        seckillOrderService.findAll().success(
            function(response){
                $scope.list=response;
            }
        );
    };

    //分页
    $scope.findPage=function(page,rows){
        seckillOrderService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne=function(id){
        seckillOrderService.findOne(id).success(
            function(response){
                $scope.entity= response;
            }
        );
    };

    //批量删除
    $scope.dele=function(){
        //获取选中的复选框
        seckillOrderService.dele( $scope.selectIds ).success(
            function(response){
                if(response.flag){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.searchEntity={};//定义搜索对象

    //搜索
    $scope.search=function(page,rows){

        seckillOrderService.search(page,rows,$scope.searchEntity).success(
            function(response){
                var str = JSON.stringify(response);
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    };

    $scope.createExcel = function () {
        seckillOrderService.createExcel($scope.list).success(
            function (response) {
                if (response.flag) {
                    location.href = response.message;
                } else {
                    alert(response.message);
                }
            }
        );
    };

    $scope.statusList={data:[{id:0,text:""},{id:1,text:"未付款"},{id:2,text:"已付款"},{id:3,text:"未发货"},{id:4,text:"已发货"},{id:5,text:"交易成功"},{id:6,text:"交易关闭"},{id:7,text:"待评价"}]};//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价

    $scope.changeEntityStatus=function (x) {
        if(x==0){
            $scope.searchEntity.status="";
        }else{
            $scope.searchEntity.status=x;
        }
    };
    $scope.changeEntitySourceType=function (x) {
        if(x==0){
            $scope.searchEntity.sourceType="";
        }else{
            $scope.searchEntity.sourceType=x;
        }
    };

    $scope.changeReceiver=function(){

        //正则检验 姓名/手机号
        const regexInteger = /^\d+$/gi;
        // alert(regexInteger.test($scope.receiverRegex));
        if(regexInteger.test($scope.receiverRegex)){
            $scope.searchEntity.receiverMobile=$scope.receiverRegex;
        }else{
            $scope.searchEntity.receiverMobile="";
            $scope.searchEntity.receiver=$scope.receiverRegex;
        }
    }
});