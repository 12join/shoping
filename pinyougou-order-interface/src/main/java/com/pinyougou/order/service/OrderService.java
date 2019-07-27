package com.pinyougou.order.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

import com.pinyougou.pojo.group.UserOrder;
import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加,修改了返回值类型，在创建订单时将支付订单号返回给前端，用map封装
	*/
	public Map add(TbOrder order);
	
	
	/**
	 * 修改
	 */
	public void update(TbOrder order);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbOrder order, int pageNum,int pageSize);
	
	/**
	 * 根据用户ID获取支付日志
	 * @param userId
	 * @return
	 */
	public TbPayLog searchPayLogFromRedis(String userId);
	
	
	/**
	 * 支付成功修改状态
	 * @param out_trade_no
	 * @param transaction_id
	 */
	public void updateOrderStatus(String out_trade_no,String transaction_id);


	/**
	 * 根据订单号获取到对应的支付订单号
	 * @param orderId
	 * @return
	 */
	public String getPayLogId(String orderId);


	/**
	 * 当前用户所有状态的订单列表
	 * @param userId
	 * @return
	 */
	public PageResult findOrderByUserId(String userId,int pageNum, int pageSize);



    /**
     * 当前用户指定状态的订单列表
     * @param userId
     * @param status
     * @return
     */
    PageResult findOrderByUserId(String userId,String[] status,int pageNum, int pageSize);


//	public List<UserOrder> findOrderByUserIdAndStatus(String userId,String[] status);


	/**
	 * 根据支付订单号更新支付日志信息，改变订单状态
	 * @param payLogId 支付订单号
	 * @return
	 */
	public void cancelOrder(String payLogId);


	/**
	 * 确认收货
	 *
	 * @param orderId
	 */
	public void confirmOrder(String orderId);


	/**
	 * 提醒发货
	 * @param orderId
	 */
	public void remindSend(String orderId);

	/**
	 * 延长收货
	 * @param orderId
	 */
	public void delayReceive(String orderId);

	/**
	 * 根据订单号删除订单
	 * @param orderId
	 */
	public void deleOrder(String orderId);



	/**
	 * 获取订单详情
	 * @param orderId
	 * @return
	 */
	public UserOrder orderDetail(String orderId);


	/**
	 * 通过payLogId获取订单Id
	 * @param payLogId
	 * @return
	 */
	public String getOrderId(String payLogId);


	/**
	 * 查询所有的支付日志
	 *
	 */
	public List<TbPayLog> getPayLogList();

	/**
	 * 跟新指定id订单状态
	 * @param orderId
	 * @param status
	 */
	public void updateStatus(Long orderId,String status);

}
