package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.group.UserOrder;
import com.pinyougou.user.service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;
    /**
     * 获取用户所有状态的订单信息
     * @return
     */
    @RequestMapping("/userOrder")
    public List<UserOrder> findUserOrder(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserOrder> orderList = orderService.findOrderByUserId(userId);
        return orderList;
    }
}