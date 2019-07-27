app.controller('loginBack' ,function($scope){

    $scope.loginBack=function() {

        var backUrl = window.location.search;
        // alert(backUrl);
        // alert(decodeURIComponent(backUrl.substring(backUrl.indexOf("backUrl=")+8), "UTF-8"));
        location.href=decodeURIComponent(backUrl.substring(backUrl.indexOf("backUrl=")+8), "UTF-8");

        // var allUrl= decodeURIComponent(backUrl.substring(backUrl.indexOf("backUrl=")+8), "UTF-8");
        // alert(allUrl.substring(allUrl.indexOf("\/")+1));
        // location.href=allUrl.substring(allUrl.indexOf("\/")+1);
    }

});
