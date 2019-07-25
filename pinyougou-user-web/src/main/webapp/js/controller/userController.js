//控制层
app.controller('userController', function ($scope, userService, loginService,uploadService) {

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


    $scope.findUser = function () {
        userService.findUser().success(
            function (response) {
                $scope.userInfo = response;
            }
        );
    }


    $scope.str1 = "1990";
    $scope.str2 = "4";
    $scope.str3 = "3";
    // $scope.str=$scope.str1+"-"+$scope.str2+"-"+$scope.str3;

    $scope.addStr1 = "安徽省";
    // $scope.addStr2="北京市市辖区";
    // $scope.addStr3="东城区";
    $scope.addStr4 = "";
    $scope.inStr = "图书音像/电子书刊";


    $scope.updateInfo = function () {
        $scope.userInfo.birthday = $scope.str1 + "-" + $scope.str2 + "-" + $scope.str3;
        $scope.userInfo.address = $scope.addStr1 + $scope.addStr2 + $scope.addStr3 + $scope.addStr4;
        $scope.userInfo.interest = $scope.inStr;
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
    }


    $scope.uploadFile = function () {
        // 调用uploadService的方法完成文件的上传
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    // 获得url
                    $scope.userInfo.headPic = response.message;
                    alert("保存成功")
                } else {
                    alert(response.message);
                }
            });
    }


});	
