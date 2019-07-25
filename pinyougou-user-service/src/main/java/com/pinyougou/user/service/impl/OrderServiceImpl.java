package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbOrderItemExample;
import com.pinyougou.pojo.group.UserOrder;
import com.pinyougou.user.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;


    @Override
    public List<UserOrder> findOrderByUserId(String userId) {
        List<UserOrder> orderList = new ArrayList<>();
        UserOrder userOrderList = new UserOrder();
        TbOrderExample example = new TbOrderExample();
        TbOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        example.setOrderByClause("status");

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
    }
}
