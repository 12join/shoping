package com.pinyougou.pojo.group;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.core.annotation.Order;

import java.io.Serializable;
import java.util.List;

public class UserOrder implements Serializable{
    private TbOrder order;
    private List<TbOrderItem> orderItemList;


    public TbOrder getOrder() {
        return order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
