package com.pinyougou.user.controller;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.group.UserOrder;
import entity.PageResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;





import entity.Result;
@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;


    /**
     * 查询所有的支付日志，在支付订单时使用
     * @return
     */
    @RequestMapping("/getPayLogList")
    public List<TbPayLog> getPayLogList() {
        return orderService.getPayLogList();
    }

    /**
     * 获取用户所有状态的订单信息
     * @return
     */
    @RequestMapping("/userOrder")
    public PageResult findUserOrder(int page){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.findOrderByUserId(userId,page,5);
    }

    /**
     * 根据订单状态查询订单列表
     * @param status
     * @return
     */
    @RequestMapping("/orderStatus")
    public PageResult orderStatus(String[] status,int page){

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return orderService.findOrderByUserId(userId,status,page,5);
    }


    /**
     * 获取分页查询结果
     * @return
     */
//    @RequestMapping("/search")
//    public PageBean findPage(@RequestBody TbOrder tbOrder, int page,int rows) {
//        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
//        tbOrder.setUserId(userId);
////		PageBean pageBean = orderService.findPage(tbOrder,page, rows);
////		int totalPage = (int) Math.ceil((pageBean.getTotalCount()/rows));
////		pageBean.setCurrentPage(page);
////		pageBean.setPageSize(rows);
////		pageBean.setTotalPage(totalPage);
////
//
//
//        return  null;
//    }

    /**
     * 前端主动取消订单操作
     * @return
     */
    @RequestMapping("/cancelOrder")
    public Result cancelOrder() {
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            orderService.cancelOrder(userId);
            return new Result(true, "订单取消成功");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new Result(false, "取消失败");
        }

    }

    /**
     * 确认收货
     * @param orderId
     * @return
     */
    @RequestMapping("/confirmOrder")
    public Result confirmOrder(String orderId) {

        try {
            orderService.confirmOrder(orderId);
            return new Result(true, "确认收货成功");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new Result(false, "确认收货异常");
        }
    }

    /**
     * 根据订单号删除订单以及订单明细
     * @param orderId
     * @return
     */
    @RequestMapping("/deleOrder")
    public Result deleOrder(String orderId) {
        try {
            orderService.deleOrder(orderId);
            return new Result(true, "订单已删除");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new Result(false, "操作失败");
        }
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @RequestMapping("/orderDetail")
    public UserOrder orderDetail(String orderId) {
        UserOrder orderDetail = orderService.orderDetail(orderId);
        return orderDetail;
    }

    /**
     * 通过支付日志获取到对应订单的ID用来查询用户的详细信息
     * @param payLogId
     * @return
     */
    @RequestMapping("/getOrderId")
    public String getOrderId(String payLogId) {
        return orderService.getOrderId(payLogId);
    }



}
