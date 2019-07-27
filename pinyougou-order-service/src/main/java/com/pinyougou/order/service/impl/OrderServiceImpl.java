package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.*;

import com.pinyougou.pojo.*;
import com.pinyougou.pojo.group.UserOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.group.Cart;
import com.pinyougou.order.service.OrderService;

import entity.PageResult;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	/**
	 * 增加
	 * 修改内容：在生成订单时，以支付订单号为键，将支付日志放入缓存，解决同一用户下两个订单时，新订单日志会覆盖旧订单日志的问题
	 */
	@Override
	public Map add(TbOrder order) {
		
		//1.从redis中提取购物车列表
		List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		
		List<String> orderIdList=new ArrayList();//订单ID集合
		double total_money=0;//总金额
		//2.循环购物车列表添加订单
		for(Cart  cart:cartList){
			TbOrder tbOrder=new TbOrder();
			long orderId = idWorker.nextId();	//获取ID		
			tbOrder.setOrderId(orderId);
			tbOrder.setPaymentType(order.getPaymentType());//支付类型
			tbOrder.setStatus("1");//未付款 
			tbOrder.setCreateTime(new Date());//下单时间
			tbOrder.setUpdateTime(new Date());//更新时间
			tbOrder.setUserId(order.getUserId());//当前用户
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货人地址
			tbOrder.setReceiverMobile(order.getReceiverMobile());//收货人电话
			tbOrder.setReceiver(order.getReceiver());//收货人
			tbOrder.setSourceType(order.getSourceType());//订单来源
			//这里商家ID获取不到因为来源是cart.getSellerId()才对
			tbOrder.setSellerId(cart.getSellerId());//商家ID
			
			double money=0;//合计数
			//循环购物车中每条明细记录
			for(TbOrderItem orderItem:cart.getOrderItemList()  ){
				orderItem.setId(idWorker.nextId());//主键
				orderItem.setOrderId(orderId);//订单编号
				orderItem.setSellerId(cart.getSellerId());//商家ID
				orderItemMapper.insert(orderItem);				
				money+=orderItem.getTotalFee().doubleValue();
			}
			
			tbOrder.setPayment(new BigDecimal(money));//合计
			
			
			orderMapper.insert(tbOrder);
			
			orderIdList.add(orderId+"");
			total_money+=money;
		}
		Map map = new HashMap();
		//添加支付日志-微信
		if("1".equals(order.getPaymentType())){
			TbPayLog payLog=new TbPayLog();
			
			payLog.setOutTradeNo(idWorker.nextId()+"");//支付订单号
			payLog.setCreateTime(new Date());
			payLog.setUserId(order.getUserId());//用户ID
			payLog.setOrderList(orderIdList.toString().replace("[", "").replace("]", ""));//订单ID串
			payLog.setTotalFee( (long)( total_money*100)   );//金额（分）
			payLog.setTradeState("0");//交易状态
			payLog.setPayType("1");//微信
			payLogMapper.insert(payLog);
			
			redisTemplate.boundHashOps("payLog").put(payLog.getOutTradeNo(), payLog);//支付订单号为键，放入缓存
			map.put("payLogId",payLog.getOutTradeNo());
		}

		//添加支付日志-支付宝
		if("3".equals(order.getPaymentType())){
			TbPayLog payLog=new TbPayLog();
			payLog.setOutTradeNo(idWorker.nextId()+"");//支付订单号
			payLog.setCreateTime(new Date());
			payLog.setUserId(order.getUserId());//用户ID
			payLog.setOrderList(orderIdList.toString().replace("[", "").replace("]", ""));//订单ID串
			payLog.setTotalFee( (long)( total_money*100)   );//金额（分）
			payLog.setTradeState("0");//交易状态
			payLog.setPayType("3");//支付宝
			payLogMapper.insert(payLog);

			redisTemplate.boundHashOps("payLog").put(payLog.getOutTradeNo(), payLog);//支付订单号为键,放入缓存
			map.put("payLogId",payLog.getOutTradeNo());
		}
		
		
		//3.清除redis中的购物车
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
		return map;

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public TbPayLog searchPayLogFromRedis(String payLogId) {
		return (TbPayLog) redisTemplate.boundHashOps("payLog").get(payLogId);
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		//1.修改支付日志的状态及相关字段
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date());//支付时间
		payLog.setTradeState("1");//交易成功
		payLog.setTransactionId(transaction_id);//微信的交易流水号
		
		payLogMapper.updateByPrimaryKey(payLog);//修改
		//2.修改订单表的状态
		String orderList = payLog.getOrderList();// 订单ID 串
		String[] orderIds = orderList.split(",");
		
		for(String orderId:orderIds){
			TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
			order.setStatus("2");//已付款状态
			order.setPaymentTime(new Date());//支付时间
			orderMapper.updateByPrimaryKey(order);			
		}
		
		//3.清除缓存中的payLog/通过支付订单号
		redisTemplate.boundHashOps("payLog").delete(out_trade_no);
		
	}


	/**
	 * 通过orderId来获取支付订单号（暂时不用）
	 * @param orderId
	 * @return
	 */
	@Override
	public String getPayLogId(String orderId) {
		List<TbPayLog> list = payLogMapper.selectByExample(null);
		List orderList = new ArrayList<>();
		for (TbPayLog tbPayLog : list) {
			String orderListString = tbPayLog.getOrderList();
			if(orderListString.contains(orderId)) {
				return tbPayLog.getOutTradeNo();
			}
		}
		return null;
	}



	/**
	 * 通过当前登录用户获取到对应的订单信息
	 */
	@Override
	public PageResult findOrderByUserId(String userId,int pageNum, int pageSize) {
		List<UserOrder> orderList = new ArrayList<>();
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		example.setOrderByClause("status");
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> pageOrder = (Page<TbOrder>)orderMapper.selectByExample(example);

		for (TbOrder order : pageOrder.getResult()) {//通过OrderId查询出对应的订单详情表
			TbOrderItemExample orderItemExample = new TbOrderItemExample();
			com.pinyougou.pojo.TbOrderItemExample.Criteria orderItemCriteria = orderItemExample.createCriteria();
			orderItemCriteria.andOrderIdEqualTo(order.getOrderId());
			//orderItemCriteria.andSellerIdEqualTo(order.getSellerId());
			List<TbOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample );
			UserOrder userOrder = new UserOrder();
			userOrder.setOrder(order);
			userOrder.setOrderItemList(orderItemList);
			//map.put("order", order);
			//map.put("orderItemList", orderItemList);
			orderList.add(userOrder);
		}

        return new PageResult(pageOrder.getTotal(),orderList);

	}


    @Override
    public PageResult findOrderByUserId(String userId,String[] status,int pageNum, int pageSize) {
        List<UserOrder> orderList = new ArrayList<>();
        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<String> list = Arrays.asList(status);
        //只加了一个查询条件，其实可以通过判断是否有status值来决定是否通过状态查询，这里有冗余，懒得改了
        criteria.andStatusIn(list);

        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> pageOrder = (Page<TbOrder>)orderMapper.selectByExample(example);

        for (TbOrder order : pageOrder.getResult()) {//通过OrderId查询出对应的订单详情表
            TbOrderItemExample orderItemExample = new TbOrderItemExample();
            com.pinyougou.pojo.TbOrderItemExample.Criteria orderItemCriteria = orderItemExample.createCriteria();
            orderItemCriteria.andOrderIdEqualTo(order.getOrderId());
            //orderItemCriteria.andSellerIdEqualTo(order.getSellerId());
            List<TbOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample );
            UserOrder userOrder = new UserOrder();
            userOrder.setOrder(order);
            userOrder.setOrderItemList(orderItemList);
            //map.put("order", order);
            //map.put("orderItemList", orderItemList);
            orderList.add(userOrder);
        }

        return new PageResult(pageOrder.getTotal(),orderList);

    }


	/**
	 * 当前用户指定状态的订单列表
	 */
	/*@Override
	public List<UserOrder> findOrderByUserIdAndStatus(String userId, String[] status) {
		List<UserOrder> orderList = new ArrayList<>();
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);

		List<String> list = Arrays.asList(status);
		//只加了一个查询条件，其实可以通过判断是否有status值来决定是否通过状态查询，这里有冗余，懒得改了
		criteria.andStatusIn(list);

		List<TbOrder> tbOrderList = orderMapper.selectByExample(example );

		for (TbOrder order : tbOrderList) {//通过OrderId查询出对应的订单详情表
			TbOrderItemExample orderItemExample = new TbOrderItemExample();
			com.pinyougou.pojo.TbOrderItemExample.Criteria orderItemCriteria = orderItemExample.createCriteria();
			orderItemCriteria.andOrderIdEqualTo(order.getOrderId());
			orderItemCriteria.andSellerIdEqualTo(order.getSellerId());
			List<TbOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample );
			UserOrder userOrder = new UserOrder();
			userOrder.setOrder(order);
			userOrder.setOrderItemList(orderItemList);
			//map.put("order", order);
			//map.put("orderItemList", orderItemList);
			orderList.add(userOrder);
		}
		return orderList;
	}*/


	/**
	 * 取消订单操作
	 */
	@Override
	public void cancelOrder(String userName) {

		TbPayLog redisPayLog = searchPayLogFromRedis( userName);//还是需要，因为需要此交易订单号，来更新商家订单状态
		redisTemplate.boundHashOps("payLog").delete(userName);
		String[] orders = redisPayLog.getOrderList().split(",");
		for(String order:orders) {

			if(order!=null) {
				TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(order));
				//tbOrder.setPaymentTime(new Date());
				tbOrder.setStatus("6");
				tbOrder.setUpdateTime(new Date());
				tbOrder.setCloseTime(new Date());
				orderMapper.updateByPrimaryKey(tbOrder);
			}
		}
	}


	/**
	 * 确认收货
	 */
	@Override
	public void confirmOrder( String orderId) {
		TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
		//tbOrder.setPaymentTime(new Date());
		tbOrder.setStatus("7");
		tbOrder.setUpdateTime(new Date());
		tbOrder.setEndTime(new Date());
		orderMapper.updateByPrimaryKey(tbOrder);

	}


	/**
	 * 根据订单号删除订单以及订单明细
	 */
	@Override
	public void deleOrder(String orderId) {
		//删除订单

		orderMapper.deleteByPrimaryKey(Long.valueOf(orderId));
		//删除订单明细
		TbOrderItemExample example = new TbOrderItemExample();
		com.pinyougou.pojo.TbOrderItemExample.Criteria criteria = example.createCriteria();

		criteria.andOrderIdEqualTo(Long.valueOf(orderId));
		// TODO Auto-generated method stub
		orderItemMapper.deleteByExample(example );

	}

	/**
	 * 订单详情
	 */
	@Override
	public UserOrder orderDetail(String orderId) {
		UserOrder userOrder = new UserOrder();
		TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
		TbOrderItemExample example = new TbOrderItemExample();
		com.pinyougou.pojo.TbOrderItemExample.Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(Long.valueOf(orderId));
		List<TbOrderItem> orderItemList = orderItemMapper.selectByExample(example);
		userOrder.setOrder(order);
		userOrder.setOrderItemList(orderItemList);
		return userOrder;
	}


	/**
	 * 通过payLogId获取订单Id
	 */
	@Override
	public String getOrderId(String payLogId) {
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(payLogId);
		String orderList = payLog.getOrderList();
		String[] orderIds = orderList.split(",");
		return orderIds[0];
	}

	/**
	 * 查询所有的支付日志，在支付时使用
	 * @return
	 */
	@Override
	public List<TbPayLog> getPayLogList() {
		List<TbPayLog> payLogList = payLogMapper.selectByExample(null);
		return  payLogList;
	}



}
