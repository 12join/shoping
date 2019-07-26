package com.pinyougou.user.dao.impl;

import com.pinyougou.pojo.group.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FavoriteDao extends MongoRepository<Favorite,String> {
}
