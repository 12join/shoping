package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.group.Favorite;
import com.pinyougou.user.dao.impl.FavoriteDao;
import com.pinyougou.user.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import java.util.Date;
import java.util.List;
@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 查询所有收藏
     * @return
     */
    @Override
    public List<Favorite> findbyUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        List<Favorite> all = mongoTemplate.find(query,Favorite.class,"Favourites");
        return all;
    }

    /**
     * 添加一个收藏
     * @param goods
     */
    @Override
    public void save(TbGoods goods, TbGoodsDesc goodsDesc,String name) {
        Favorite  favorite = new Favorite(idWorker.nextId()+"",name,goods.getId()+"",new Date(),goods,goodsDesc);
        System.out.println(favorite);
        mongoTemplate.insert(favorite,"Favourites");
    }
    @Override
    public Favorite findOne(String id){
       return mongoTemplate.findById(id,Favorite.class,"Favourites");
    }

    @Override
    public void delete(String id) {
        Query query=new Query(Criteria.where("_id").is(id));
        int favorite = mongoTemplate.remove(query, Favorite.class, "Favourites").getN();
        System.out.println(favorite);

    }

    @Override
    public void deleteByGoodsId(TbGoods goods, String name) {
        Query query=new Query();
        query.addCriteria(Criteria.where("username").is(name)).addCriteria(Criteria.where("goodsId").is(goods.getId()+""));
        int favorite = mongoTemplate.remove(query, Favorite.class, "Favourites").getN();
        System.out.println(favorite);

    }

    @Override
    public List<Favorite> findbyGoods(TbGoods goods,String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(name)).addCriteria(Criteria.where("goodsId").is(goods.getId()+""));
        List<Favorite> all = mongoTemplate.find(query,Favorite.class,"Favourites");
        return all;
    }


}
