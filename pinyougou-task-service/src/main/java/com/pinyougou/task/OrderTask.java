package com.pinyougou.task;

import com.pinyougou.mapper.TbOrderMapper;

import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbOrder;

import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class OrderTask {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrder order;

    /**
     * 定时取消订单
     */
    @Scheduled(cron = "* * * * * ?")
    public void autoCancelOrder(){
        System.out.println("开始检查是否有过期订单");
        List<TbPayLog> payLogList = payLogMapper.selectByExample(null);
        for (TbPayLog tbPayLog : payLogList) {
            Date createTime = tbPayLog.getCreateTime();
            Date payTime = tbPayLog.getPayTime();
            Date now = new Date();
            long mins = 60*1*1000;
            if(payTime==null){

                if(now.getTime()-createTime.getTime()>mins){
                    //如果创建的支付订单在15分钟内没有支付，则取消订单，清除缓存
                    String orderList = tbPayLog.getOrderList();
                    String[] split = orderList.split(",");
                    for (String s : split) {
                        TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(s));
                        if(!"6".equals(order.getStatus())){

                        order.setStatus("6");
                        order.setUpdateTime(new Date());
                        order.setCloseTime(new Date());
                        orderMapper.updateByPrimaryKey(order);
                        redisTemplate.boundHashOps("payLog").delete(tbPayLog.getOutTradeNo());
                        System.out.println("已取消订单，清除缓存");
                        }
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ? ")
    public void autoReceive(){

        System.out.println("开始检查是否有");
        List<TbOrder> orderList = orderMapper.selectByExample(null);
        for (TbOrder tbOrder : orderList) {
            Date consignTime = tbOrder.getConsignTime();
            Date date = new Date();
            long days = 60*60*24*10*1000;
            if(date.getTime()-consignTime.getTime()>days){
                if(!"7".equals(tbOrder.getStatus())){
                    tbOrder.setStatus("7");
                    tbOrder.setUpdateTime(new Date());
                    tbOrder.setEndTime(new Date());
                    orderMapper.updateByPrimaryKey(tbOrder);
                    System.out.println("超过10天自动收货");
                }
            }

        }

    }
}
