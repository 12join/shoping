//首页控制器
app.controller('indexController',function($scope,loginService){
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
});