package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;

public interface SeckillOrderService {

	
	/**
	 * 提交抢购订单
	 * @param seckillId
	 * @param userId
	 */
	public void submitOrder(Long seckillId, String userId);
	
	
	/**
	* 根据用户名查询秒杀订单
	* @param userId
	*/
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId);
	
	/**
	 * 如果付款成功,将redis的订单存入数据库
	 * @param userId  key
	 * @param orderId  安全检验订单id与redis中的id是否相符
	 * @param transactionId
	 */
	public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);
	
	/**
	 * 若订单超时,从redis中删除订单
	 * @param userId
	 * @param orderId
	 */
	public void deleteOrderFromRedis(String userId, Long orderId);
}
