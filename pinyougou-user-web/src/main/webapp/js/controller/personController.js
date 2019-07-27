//首页控制器
app.controller('personController',function($scope,loginService,favoriteService){
    //获取用户登陆信息
    $scope.user={name:"",isLogin:false};
    $scope.showName=function(){

        loginService.showName().success(

            function(data){

                $scope.user.name=data.loginName;

                $scope.user.isLogin=data.isLogin;

            }
        );
    }
    $scope.connect=[];
    $scope.findList=function () {
        favoriteService.findAll().success(function (data) {

            for (var i=0;i<data.length;i++){
                data[i].goodsDesc.itemImages=JSON.parse(data[i].goodsDesc.itemImages);
            }
            $scope.connect=data;
        })
    }
});