app.controller('addressController', function ($scope, addressService,loginService) {


    //获取用户登陆信息
    $scope.user={name:"",isLogin:false};
    $scope.showName=function(){

        loginService.showName().success(

            function(data){

                $scope.user.name=data.loginName;

                $scope.user.isLogin=data.isLogin;

            }
        );
    };






    $scope.address={};


    $scope.findAddressList=function(){
        addressService.findAddressList().success(
            function(response){
                $scope.addressList=response;
            }
        );
    };


    $scope.save=function(){
        // $scope.address.addresss=$scope.addStr1+" "+$scope.addStr2+" "+$scope.addStr3+" "+$scope.addStr4;

        var serviceObject;//服务层对象
        if($scope.address.userId!=null){//如果有ID
            serviceObject=addressService.update($scope.address); //修改
        }else{
            serviceObject=addressService.add($scope.address);//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    alert(response.message);
                    $scope.findAddressList();//重新加载
                    $scope.address={};
                }else{
                    alert(response.message);
                }
            }
        );
    }

    $scope.ids=[];
    //删除
    $scope.dele=function(id){
        if(!confirm("你确定要删除吗")){
            return;
        }
        $scope.ids.push(id);
        addressService.dele($scope.ids).success(
            function(response){
                if(response.success){
                    $scope.findAddressList();//重新加载
                    $scope.ids=[];
                }
            }
        );
    }

    //查找一个
    $scope.findOne=function(address){
        $scope.address=JSON.parse(JSON.stringify(address));


        $scope.$watch('address.provinceId',function(newValue,oldValue){
            if(newValue != undefined){
                addressService.findC(newValue).success(
                    function(response){
                        $scope.areasList={};
                        $scope.cityList=response;

                    }
                );
            }
        })

        $scope.$watch('address.cityId',function(newValue,oldValue){
            if(newValue != undefined){
                addressService.findA(newValue).success(
                    function(response){
                        $scope.areasList=response;
                    }
                );
            }
        })


    };

    $scope.findP=function () {
        addressService.findP().success(
            function(response){
                $scope.provinceList=response;
            }
        );
    };

    // $scope.findC=function (id) {
    //     addressService.findC(id).success(
    //         function(response){
    //             $scope.cityList=response;
    //         }
    //     );
    // };
    //
    // $scope.findA=function (id) {
    //     addressService.findA(id).success(
    //         function(response){
    //             $scope.areasList=response;
    //         }
    //     );
    // }


});