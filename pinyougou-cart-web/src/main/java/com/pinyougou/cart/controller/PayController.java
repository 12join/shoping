package com.pinyougou.cart.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.pinyougou.pay.service.AliPayService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.Result;
import util.IdWorker;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Reference
	private WeixinPayService weixinPayService;
	
	@Reference
	private OrderService orderService;


	@Reference
	private AliPayService aliPayService;

	/**
	 * 微信生成二维码
	 * @return map集合 包含支付订单号，支付金额
	 *  修改方法，因为生成订单时，支付日志是以支付订单号为键存入缓存的，所以要以此取出支付日志
	 *  方法添加参数，为前端传递的支付订单号
	 */
	@RequestMapping("/createNative")
	public Map createNative(String payLogId){
		//1.获取当前登录用户
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		//2.提取支付日志（从缓存 ）通过支付订单号查询支付日志
		TbPayLog payLog = orderService.searchPayLogFromRedis(payLogId);
		//3.调用微信支付接口
		if(payLog!=null){
			return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee()+"");		
		}else{
			return new HashMap<>();
		}		
	}

	/**
	 * 微信支付结果查询
	 * @param out_trade_no 支付订单号
	 * @return 查询结果
	 */
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no){
		Result result=null;
		int x=0;
		while(true){
			
			Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);//调用查询
			if(map==null){
				result=new Result(false, "支付发生错误");
				break;
			}
			if(map.get("trade_state").equals("SUCCESS")){//支付成功
				result=new Result(true, "支付成功");				
				orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));//修改订单状态
				break;
			}
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			x++;
			if(x>=100){				
				result=new Result(false, "二维码超时");
				break;				
			}
			
		}
		return result;
	}


	/**
	 * 根据返回的支付地址生成支付二维码
	 * @return
	 */
	@RequestMapping("/createAliPayNative")
	public Map createAliPayNative(String payLogId) {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		TbPayLog payLog = orderService.searchPayLogFromRedis(payLogId);
		if(payLog!=null) {
			return aliPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee()+"");
		}
		return new HashMap<>();

	}

	/**
	 * 支付宝回调信息，要是用工具进行内网穿透
	 * @param request
	 * @return
	 */
	@RequestMapping("/alipayCallBack")
	public String alipayCallBack(HttpServletRequest request) {
		System.out.println("===================");
		System.out.println(request.getParameterMap());
		Map<String,String> params = new HashMap();
		Map requestParams = request.getParameterMap();
		for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
			String name = (String)iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for(int i = 0 ; i <values.length;i++){

				valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
			}
			params.put(name,valueStr);
		}
		if("TRADE_SUCCESS".equals(params.get("trade_status"))) {
			orderService.updateOrderStatus(params.get("out_trade_no")+"", params.get("trade_no"));//修改订单状态
		}
		return "success";
	}

	/**
	 * 通过订单号循环调用支付宝查询接口获取支付状态
	 * @param out_trade_no
	 * @return
	 */
	@RequestMapping("/queryAliPayStatus")
	public Result queryAliOrderStatus(String out_trade_no) {
		Result result =null;
		int x=0;
		while(true) {
			Map map = aliPayService.queryOrderStatus(out_trade_no);
			if(map==null) {
				return new Result(false, "支付出错");
			}
			if(map.get("trade_state").equals("TRADE_SUCCESS")) {

				orderService.updateOrderStatus(out_trade_no, map.get("transaction_id")+"");
				return new Result(true, "支付成功");
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			x++;
			if(x>100) {
				return new Result(false, "二维码超时");
			}
		}
	}
	
	
}
