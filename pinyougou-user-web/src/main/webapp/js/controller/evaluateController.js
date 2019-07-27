//首页控制器
app.controller('evaluateController',function($scope,evaluateService,loginService,$location,orderService,uploadService){
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

    $scope.save=function () {
        getEntity();
        var evaluate = $scope.entity;
        alert(evaluate);
        evaluateService.save(evaluate).success(function (data) {
            alert(data.message);
            location.href="/home-index.html"
        });
    }
    $scope.findOrder=function () {
        orderService.findOne($location.search()['orderId']).success(function (data) {
           $scope.order=data.order;
           $scope.orderItem=data.orderItem;
        });
    }

    $scope.options={"描述相符":"","发货迅速":"","快递满分":"","服务周到":""};
    //判断某选项是否被选中
    $scope.isSelected=function(key,value){
        if($scope.options[key]==value){
            return true;
        }else{
            return false;
        }
    }

    //用户选择选项
    $scope.selectOptiOns=function(key){
        if( $scope.options[key]=='true'){
            $scope.options[key]='false';
        }else {
            $scope.options[key]='true';
        }
    }
    /*
    * @Id
    private String _id;
    private String username; //用户ID
    private String goodsId; //商品Id
    private Date evaluateTime; //评价时间
    private String orderId; //订单Id
    private String text; //评价内容
    private List<String> star; //评价选项*/
    $scope.entity={"_id":"","username":"","goodsId":"","evaluateTime":"","orderId":"","text":"","star":[],"image":[]};

     


    var getEntity=function () {
        $scope.entity.goodsId=$scope.orderItem.goodsId;
        $scope.entity.orderId=$scope.orderItem.orderId;
        if( $scope.options.发货迅速=="true"){
            $scope.entity.star.push('发货迅速');
        }
        if( $scope.options.描述相符=="true"){
            $scope.entity.star.push('描述相符');
        }
        if( $scope.options.快递满分=="true"){
            $scope.entity.star.push('快递满分');
        }
        if( $scope.options.服务周到=="true"){
            $scope.entity.star.push('服务周到');
        }
    }


    $scope.uploadFile = function(){
        // 调用uploadService的方法完成文件的上传
        uploadService.uploadFile().success(function(response){
            if(response.flag){
                // 获得url
                $scope.entity.image .push(response.message);
            }else{
                alert(response.message);
            }
        });


    }

});