app.service('payService',function($http){
	//本地支付

	this.createNative=function(type,payLogId){//根据支付类型进行不同的请求
		if(type==1){
		return $http.get('pay/createNative.do?payLogId='+payLogId);
		}
		return $http.get('pay/createAliPayNative.do?payLogId='+payLogId);
	}
	
	//查询支付状态
	this.queryPayStatus=function(out_trade_no,type){
		if(type==1){
		return $http.get('pay/queryPayStatus.do?out_trade_no='+out_trade_no);
		}
		return $http.get('pay/queryAliPayStatus.do?out_trade_no='+out_trade_no);
	}
});