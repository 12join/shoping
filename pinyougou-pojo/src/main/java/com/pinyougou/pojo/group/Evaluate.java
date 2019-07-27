package com.pinyougou.pojo.group;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Evaluate implements Serializable{
    @Id
    private String _id;
    private String username; //用户ID
    private String goodsId; //商品Id
    private Date evaluateTime; //评价时间
    private String orderId; //订单Id
    private String text; //评价内容
    private String image;
    private List<String> star; //评价选项

    public Evaluate() {
    }

    public Evaluate(String _id, String username, String goodsId, Date evaluateTime, String orderId, String text, String image, List<String> star) {
        this._id = _id;
        this.username = username;
        this.goodsId = goodsId;
        this.evaluateTime = evaluateTime;
        this.orderId = orderId;
        this.text = text;
        this.image = image;
        this.star = star;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public Date getEvaluateTime() {
        return evaluateTime;
    }

    public void setEvaluateTime(Date evaluateTime) {
        this.evaluateTime = evaluateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getStar() {
        return star;
    }

    public void setStar(List<String> star) {
        this.star = star;
    }

    @Override
    public String toString() {
        return "Evaluate{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", evaluateTime=" + evaluateTime +
                ", text='" + text + '\'' +
                ", star=" + star +
                '}';
    }
}
