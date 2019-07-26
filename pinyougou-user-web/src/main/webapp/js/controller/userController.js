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

    $scope.addStr1 = "北京市";
    $scope.addStr2="北京市市辖区";
    $scope.addStr3="东城区";
    $scope.addStr4 = "";


    $scope.updateInfo = function () {
        if(!flag){
            alert("昵称格式有误,重新输入");
            return;
        }
        if($scope.addStr4==null||$scope.addStr4==""){
            alert("地址不能为空,请输入详细地址");
            return;
        }


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


    $scope.test = function () {
        setTimeout(function () {
            $scope.addStr2=$("#city").val();
            alert($scope.addStr2);
            $scope.addStr3=$("#district").val();
            alert($scope.addStr3);
        }, 50);
    }

    $scope.test1 = function () {
        setTimeout(function () {
            $scope.addStr3=$("#district").val();
            alert($scope.addStr3);
        }, 50);
    }

    $("#address").blur(function(){
        var $address=this.value;
        if($address==null||$address==""){
            $("#aa").html("内容不能为空");
        }else{
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


    $("#nickName").blur(function() {
        var nickName=this.value;
        validNickname(nickName);
    });

    var flag;

    function validNickname(nickName) {
        if (nickName == "") {
            $("#nickName_msg").html("请输入昵称");
            flag=false;
        }
        var reg = new RegExp("^([a-zA-Z0-9_-]|[\\u4E00-\\u9FFF])+$", "g");
        var reg_number = /^[0-9]+$/; // 判断是否为数字的正则表达式
        if (reg_number.test(nickName)) {
            $("#nickName_msg").html("昵称不能设置为手机号等纯数字格式，请您更换哦^^");
            flag=false;
        } else if (nickName.replace(/[^\x00-\xff]/g, "**").length < 4 || nickName.replace(/[^\x00-\xff]/g, "**").length > 20) {
            $("#nickName_msg").html("4-20个字符，可由中英文、数字、“_”、“-”组成");
            flag=false;
        } else if (!reg.test(nickName)) {
            $("#nickName_msg").html("昵称格式不正确");
            flag=false;
        }else{
            $("#nickName_msg").html("");
            flag=true;
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




});	
