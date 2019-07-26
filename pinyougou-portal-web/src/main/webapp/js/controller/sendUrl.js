app.controller("sendUrl",function($scope,$location){
    //获取金额
    $scope.getUrl=function(){
        var sendUrl = $location.search()['url'];
        alert(sendUrl);
        location.href=sendUrl;
    }
});