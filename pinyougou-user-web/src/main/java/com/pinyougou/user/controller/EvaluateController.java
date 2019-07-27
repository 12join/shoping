package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.group.Evaluate;
import com.pinyougou.user.service.EvaluateService;
import entity.Result;
import org.opensaml.ws.wssecurity.impl.SecurityTokenReferenceImpl;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/evaluate")
public class EvaluateController {
    @Reference
    private EvaluateService evaluateService;
    @Reference
    private OrderService orderService;
    @RequestMapping("/save")
    public Result save(@RequestBody Evaluate evaluate){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        evaluate.setUsername(username);
        try {
            evaluate.setEvaluateTime(new Date());
            System.out.println(evaluate);
            evaluateService.save(evaluate);
            orderService.updateStatus(Long.valueOf(evaluate.getOrderId()),"8");
            return new Result(true,"保存成功");
        }catch (Exception e){
            return new Result(false,"保存失败");
        }
    }
    @RequestMapping("/find")
    public List<Evaluate> find(String id){
       return evaluateService.findAll(id);
    }

    @RequestMapping("/delete")
    public Result delete(String id){
        try {
            evaluateService.delete(id);
            return new Result(true,"成功");
        }catch (Exception e){
            return new Result(false,"失败");
        }

    }



}
