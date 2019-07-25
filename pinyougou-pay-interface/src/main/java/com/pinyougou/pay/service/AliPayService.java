package com.pinyougou.pay.service;

import java.util.Map;

public interface AliPayService {
	/**
	 * 创建二维码
	 * @param out_trade_no 支付订单号
	 * @param total_fee 订单总金额
	 * @return
	 */
	public Map  createNative(String out_trade_no, String total_fee);
	
	/**
	 * 通过订单号查询订单支付状态
	 * @param out_trade_no 支付订单号
	 * @return
	 */
	public Map queryOrderStatus(String out_trade_no);
	
	/**
	 * 关闭支付宝支付
	 * @param out_trade_no 支付订单号
	 * @return
	 */
	public boolean closePay(String out_trade_no,String trade_no);

}
