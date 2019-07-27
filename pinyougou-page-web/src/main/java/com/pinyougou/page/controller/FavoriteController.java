package com.pinyougou.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.group.Favorite;
import com.pinyougou.pojo.group.Goods;
import com.pinyougou.sellergoods.service.GoodsDescService;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemService;
import com.pinyougou.user.service.FavoriteService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    @Reference
    private FavoriteService favoriteService;
    @Reference
    private ItemService itemService;
    @Reference
    private GoodsService goodsService;
    @Reference
    private GoodsDescService goodsDescService;
    @RequestMapping("/findAll")
    public List<Favorite> findAll(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//得到登陆人账号
        return favoriteService.findbyUsername(name);

    }

    @RequestMapping("/findOne")
    public Favorite findOne(String id){
        return favoriteService.findOne(id);

    }

    @RequestMapping("/save")
    public Result save(Long itemId){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//得到登陆人账号
        try {
        TbItem item = itemService.findOne(itemId);
        Goods goods = goodsService.findOne(item.getGoodsId());
            TbGoodsDesc goodsDesc = goodsDescService.findOne(item.getGoodsId());
            favoriteService.save(goods.getGoods(),goodsDesc,name);
        return new  Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

    @RequestMapping("/isFavor")
    public Result isFavor(String itemId) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//得到登陆人账号
        TbItem item = itemService.findOne(Long.valueOf(itemId));
        Goods goods = goodsService.findOne(item.getGoodsId());
        try {
            List<Favorite> favorites = favoriteService.findbyGoods(goods.getGoods(), name);
            System.out.println(favorites);
            if (favorites.size() != 1) {
                return new Result(false, "失败");
            }

            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }
    @RequestMapping("/NoFavor")
    public Result NoFavor(String itemId) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//得到登陆人账号

        try {
            TbItem item = itemService.findOne(Long.valueOf(itemId));
            Goods goods = goodsService.findOne(item.getGoodsId());
            favoriteService.deleteByGoodsId(goods.getGoods(), name);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(String id){
        try {
            favoriteService.delete(id);
            return new  Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
        }


    }
}
