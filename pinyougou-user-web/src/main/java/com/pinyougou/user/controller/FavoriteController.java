package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.group.Favorite;
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
    public Result save(@RequestBody TbGoods item){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//得到登陆人账号
        try {
            favoriteService.save(item,name);
            return new  Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
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
