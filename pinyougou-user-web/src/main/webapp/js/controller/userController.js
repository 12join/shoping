//控制层
app.controller('userController', function ($scope, userService, loginService, uploadService,addressService) {

    //注册用户
    $scope.reg = function () {

        //比较两次输入的密码是否一致
        if ($scope.password != $scope.entity.password) {
            alert("两次输入密码不一致，请重新输入");
            $scope.entity.password = "";
            $scope.password = "";
            return;
        }
        //新增
        userService.add($scope.entity, $scope.smscode).success(
            function (response) {
                alert(response.message);
            }
        );
    }

    //发送验证码
    $scope.sendCode = function () {
        if ($scope.entity.phone == null || $scope.entity.phone == "") {
            alert("请填写手机号码");
            return;
        }

        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        );
    }


    //获取用户名
    $scope.user = {name: "", isLogin: false};
    $scope.showName = function () {

        loginService.showName().success(
            function (data) {

                $scope.user.name = data.loginName;

                $scope.user.isLogin = data.isLogin;

            }
        );
    }


    //查找用户
    $scope.findUser = function () {
        userService.findUser().success(
            function (response) {
                $scope.userInfo = response;
                $scope.str1 = $scope.userInfo.birthday.split("\-")[0];
                $scope.str2 = $scope.userInfo.birthday.split("\-")[1];
                $scope.str3 = $scope.userInfo.birthday.split("\-")[2];
            }
        );
    };


    //文件上传
    $scope.uploadFile = function () {
        // 调用uploadService的方法完成文件的上传
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                // 获得url
                $scope.userInfo.headPic = response.message;
                alert("保存成功");
            } else {
                alert(response.message);
            }
        });
    };



    $scope.updateInfo = function () {
        if (!flag) {
            alert("昵称格式有误,重新输入");
            return;
        }
        if ($scope.userInfo.address == null || $scope.userInfo.address == "") {
            alert("详细地址不能为空,请输入详细地址");
            return;
        }

        $scope.userInfo.birthday = $scope.str1 + "-" + $scope.str2 + "-" + $scope.str3;
        userService.updateInfo($scope.userInfo).success(
            function (response) {
                if (response.success) {
                    alert("修改成功");
                    location.reload();
                } else {
                    alert(response.message);
                }

            }
        );
    };

    $("#address").blur(function () {
        var $address = this.value;
        if ($address == null || $address == "") {
            $("#aa").html("内容不能为空");
        } else {
            $("#aa").html("");
        }
    });

    /*$("#nickName").blur(function(){
        var nickName=this.value;
        if(nickName==null||nickName==""){
            $("#bb").html("昵称内容不能为空");
        }else{
            $("#bb").html("");
        }
    });*/


    $("#nickName").blur(function () {
        var nickName = this.value;
        validNickname(nickName);
    });

    var flag;

    function validNickname(nickName) {
        if (nickName == "") {
            $("#nickName_msg").html("请输入昵称");
            flag = false;
        }
        var reg = new RegExp("^([a-zA-Z0-9_-]|[\\u4E00-\\u9FFF])+$", "g");
        var reg_number = /^[0-9]+$/; // 判断是否为数字的正则表达式
        if (reg_number.test(nickName)) {
            $("#nickName_msg").html("昵称不能设置为手机号等纯数字格式，请您更换哦^^");
            flag = false;
        } else if (nickName.replace(/[^\x00-\xff]/g, "**").length < 4 || nickName.replace(/[^\x00-\xff]/g, "**").length > 20) {
            $("#nickName_msg").html("4-20个字符，可由中英文、数字、“_”、“-”组成");
            flag = false;
        } else if (!reg.test(nickName)) {
            $("#nickName_msg").html("昵称格式不正确");
            flag = false;
        } else {
            $("#nickName_msg").html("");
            flag = true;
        }


    }

    /*$("#email").blur(function() {
        var $email = this.value;
        var myreg = /^([\.a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
        if (myreg.test($email)) {
            flag2 = true;
            $("span").eq(1).hide();
        } else {
            $("span").eq(1).html("格式不正确");
        }

    });*/
    $scope.findP=function () {
        addressService.findP().success(
            function(response){
                $scope.provinceList=response;
            }
        );
    };

    $scope.findIn=function () {
        userService.findIn().success(
            function(response){
                $scope.interestList=response;
            }
        );
    };


    $scope.$watch('userInfo.provinceId',function(newValue,oldValue){
        if(newValue != undefined){
            addressService.findC(newValue).success(
                function(response){
                    $scope.areasList={};
                    $scope.cityList=response;

                }
            );
        }
    })

    $scope.$watch('userInfo.cityId',function(newValue,oldValue){
        if(newValue != undefined){
            addressService.findA(newValue).success(
                function(response){
                    $scope.areasList=response;
                }
            );
        }
    })

});
