package com.pinyougou.pojo.group;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * 收藏对象
 */
public class Favorite implements Serializable{
    @Id
    private String _id;
    private String username; //用户ID
    private String goodsId; //商品Id
    private Date favoriteTime; //收藏时间
    private TbGoods goods; //收藏商品

    public Favorite() {
    }

    public Favorite(String _id, String username, String goodsId, Date favoriteTime, TbGoods goods) {
        this._id = _id;
        this.username = username;
        this.goodsId = goodsId;
        this.favoriteTime = favoriteTime;
        this.goods = goods;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Date getFavoriteTime() {
        return favoriteTime;
    }

    public void setFavoriteTime(Date favoriteTime) {
        this.favoriteTime = favoriteTime;
    }

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", favoriteTime=" + favoriteTime +
                ", goods=" + goods +
                '}';
    }
}
