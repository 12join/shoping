package com.pinyougou.seckill.controller;

import java.util.HashMap;
import java.util.Map;

import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference(timeout = 8888)
	private WeixinPayService weixinPayService;

	@Reference
	private SeckillOrderService seckillOrderService;

	@RequestMapping("/createNative")
	public Map createNative() {
		// 获取当前用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		// 到 redis 查询支付日志
		TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
		if (seckillOrder != null) {
			return weixinPayService.createNative(seckillOrder.getId() + "",
					(long) (seckillOrder.getMoney().doubleValue() * 100) + "");
		} else {
			return new HashMap();
		}
	}

	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no){
		String userId=SecurityContextHolder.getContext().getAuthentication().getName();
		Result result=null;
		Map map=weixinPayService.queryPayStatus(out_trade_no);
		int time=0;
		while(true) {
			if(map==null) {
				result=new Result(false, "支付出错");
				break;
			}
			if("SUCCESS".equals(map.get("trade_state"))){//如果成功
				result=new Result(true, "支付成功");
				//修改订单状态
				try {
					seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), (String)map.get("transaction_id"));
				} catch (RuntimeException e) {
					e.printStackTrace();
					result=new Result(false, e.getMessage());
				}catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			
			try {
				Thread.sleep(3000);//间隔三秒1152531002909241344
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			time++;
			if(time>10) {//三分钟失效
				result=new Result(false, "支付超时");
				Map map2 = weixinPayService.closePay(out_trade_no);
				if(map2!=null&&"FAIL".equals(map2.get("result_code"))) {
					System.out.println(map2);
					if("ORDERPAID".equals(map2.get("err_code"))) {//判断订单支付状态
						result=new Result(true, "支付成功");
						//修改订单状态
						try {
							seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), (String)map.get("transaction_id"));
						} catch (RuntimeException e) {
							e.printStackTrace();
							result=new Result(false, e.getMessage());
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if(result.isSuccess()==false) {
					seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
				}
				break;
			}
			
		}
		return result;
	}
	
}
