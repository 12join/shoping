package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbSeckillOrderExample;
import entity.PageResult;
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
    private TbSeckillOrderMapper seckillOrderMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillOrder> findAll() {
        return seckillOrderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillOrder seckillOrder){
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    @Override
    public TbSeckillOrder findOne(Long id){
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            seckillOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillOrderExample example=new TbSeckillOrderExample();
        TbSeckillOrderExample.Criteria criteria = example.createCriteria();

        if(seckillOrder!=null){
            if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
                criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
            }
            if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
                criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
            }
            if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
                criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
            }
            if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
                criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
            }
            if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
                criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
            }
            if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
                criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
            }
            if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
                criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
            }

        }

        Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

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

	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}

	/**
	 * 根据ID获取实体
	 *
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id) {
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			seckillOrderMapper.deleteByPrimaryKey(id);
		}
	}


	@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbSeckillOrderExample example = new TbSeckillOrderExample();
		TbSeckillOrderExample.Criteria criteria = example.createCriteria();

		if (seckillOrder != null) {
			if (seckillOrder.getId() != null && seckillOrder.getId() > 0) {
				criteria.andIdEqualTo(seckillOrder.getId());
			}
			if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
				criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
			}
			if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
				criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
			}
			if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
				criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
			}
			if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
				criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
			}
			if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
				criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
			}
			if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
				criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
			}
			if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
				criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
			}

		}

		Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}


}

