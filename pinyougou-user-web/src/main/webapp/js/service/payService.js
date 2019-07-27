app.service('payService',function($http){
	this.createNative=function(type,payLogId){
		if(type==3){
			return $http.get('../pay/createAliPayNative.do?payLogId='+payLogId);
		}
		return $http.get('../pay/createNative.do?payLogId='+payLogId);
	}
	
	this.queryStatus=function(out_trade_no,type){
		if(type==3){
			return $http.get('../pay/queryAliPayStatus.do?out_trade_no='+out_trade_no);
		}
		return $http.get('../pay/queryStatus.do?out_trade_no='+out_trade_no);
	}
});