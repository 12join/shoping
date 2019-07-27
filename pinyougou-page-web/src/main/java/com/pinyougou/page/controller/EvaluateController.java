package com.pinyougou.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.group.Evaluate;
import com.pinyougou.pojo.group.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemService;
import com.pinyougou.user.service.EvaluateService;
import entity.PageResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/evaluate")
public class EvaluateController {
    @Reference
    private ItemService itemService;
    @Reference
    private GoodsService goodsService;
    @Reference
    private EvaluateService evaluateService;

    @RequestMapping("/find")
    public PageResult find(String id, int page, int size){
        TbItem item = itemService.findOne(Long.valueOf(id));
        Goods goods = goodsService.findOne(item.getGoodsId());
        return evaluateService.findByGoodsId(goods.getGoods().getId()+"",page,size);
    }
}
