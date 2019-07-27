package com.pinyougou.user.service;

import com.pinyougou.pojo.group.Evaluate;
import entity.PageResult;

import java.util.List;

public interface EvaluateService {
    public void save(Evaluate evaluate);

    public List<Evaluate> findAll(String goodsId);

    public List<Evaluate> findByUsername(String username);

    public Evaluate findByOrderId(String orderId);

    public PageResult findByGoodsId(String goodsId, int page, int size);

    public void delete(String id);

    public void update(Evaluate evaluate, String id);
    public void deleteAll(String name);
}
