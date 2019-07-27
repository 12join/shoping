package com.pinyougou.user.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.group.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FavoriteService {
   public List<Favorite> findbyUsername(String username);

   public void save(TbGoods item, TbGoodsDesc goodsDesc, String name);

   public Favorite findOne(String id);

   public void delete(String id);

   public List<Favorite> findbyGoods(TbGoods goods, String name);
   public void deleteByGoodsId(TbGoods goods, String name);
}
