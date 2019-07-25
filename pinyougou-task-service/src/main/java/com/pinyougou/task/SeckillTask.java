package com.pinyougou.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

@Component
public class SeckillTask {

	
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;


	@Scheduled(cron="0 * * * * ?")//每一分钟执行一次
	public void refreshSeckillGoods(){
		System.out.println("执行了增加");
		List ids=new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
		System.out.println(ids);
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");//审核通过
		criteria.andStockCountGreaterThan(0);//剩余库存大于 0
		criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
		criteria.andEndTimeGreaterThan(new Date());//结束时间大于当前时间
		if(ids.size()>0) {
			criteria.andIdNotIn(ids);

		}
		List<TbSeckillGoods> seckillGoodsList =seckillGoodsMapper.selectByExample(example);
		for (TbSeckillGoods seckillGoods : seckillGoodsList) {
			redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
			System.out.println(seckillGoods.getId());
		}
		
		System.out.println("=====================");
	}
	
	
	@Scheduled(cron="0/5 * * * * ?")//每5秒执行一次
	public void removeSeckillGoods(){
		List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
		if(seckillGoodsList.size()>0) {
			for (TbSeckillGoods seckillGoods : seckillGoodsList) {
				if(seckillGoods.getEndTime().getTime()<new Date().getTime()) {//秒杀商品已结束
					seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//向数据库保存记录
					redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
					System.out.println("移除秒杀商品"+seckillGoods.getId());
				}
			}
		}
		System.out.println("移除结束============");
	}
	
	
	
}
