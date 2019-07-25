package com.pinyougou.user.service;

import com.pinyougou.pojo.group.UserOrder;

import java.util.List;


public interface OrderService {

    /**
     * 查询所有
     * @param
     * @return
     */
    public List<UserOrder> findOrderByUserId(String userId);
}
