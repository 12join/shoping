package com.pinyougou.seckill.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import util.IdWorker;

@Service
@Transactional
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	@Autowired
	private IdWorker idWorker;
	
	@Override
	public void submitOrder(Long seckillId, String userId) {
			TbSeckillGoods seckillGoods=(TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
			if(seckillGoods==null) {
				throw new RuntimeException("秒杀商品不存在");
			}
			Integer stockCount = seckillGoods.getStockCount();
			if(stockCount<=0) {
				throw new RuntimeException("秒杀商品已售完");
			}
			seckillGoods.setStockCount(stockCount-1);
			if(stockCount-1<=0) {
				seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
				redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
			}else {
				redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);
			}
			
			//将订单存在redis中,当支付成功后在存入数据库
			long nextId = idWorker.nextId();
			TbSeckillOrder seckillOrder = new TbSeckillOrder();
			seckillOrder.setId(nextId);
			seckillOrder.setCreateTime(new Date());
			seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
			seckillOrder.setSeckillId(seckillId);
			seckillOrder.setSellerId(seckillGoods.getSellerId());
			seckillOrder.setUserId(userId);//设置用户 ID
			seckillOrder.setStatus("0");//状态
			redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
		}

	@Override
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		return (TbSeckillOrder)redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	@Override
	public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
		TbSeckillOrder seckillOrder=(TbSeckillOrder)searchOrderFromRedisByUserId(userId);
		if(seckillOrder==null) {
			throw new RuntimeException("订单不存在");
		}
		if(seckillOrder.getId().longValue()!=orderId.longValue()) {
			throw new RuntimeException("订单存在异常");
		}
		seckillOrder.setTransactionId(transactionId);//交易流水号
		seckillOrder.setPayTime(new Date());//支付时间
		seckillOrder.setStatus("1");//状态
		seckillOrderMapper.insert(seckillOrder);//保存到数据库
		redisTemplate.boundHashOps("seckillOrder").delete(userId);//从 redis 中清除	
	}

	@Override
	public void deleteOrderFromRedis(String userId, Long orderId) {
		TbSeckillOrder seckillOrder=(TbSeckillOrder)searchOrderFromRedisByUserId(userId);
		if(seckillOrder!=null&&seckillOrder.getId().longValue()==orderId.longValue()) {
			redisTemplate.boundHashOps("seckillOrder").delete(userId);//从 redis 中清除	
		}
		TbSeckillGoods seckillGoods=(TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
		if(seckillGoods!=null){
			seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
			redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
		}
		
	}




}

