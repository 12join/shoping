package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.group.Evaluate;
import com.pinyougou.pojo.group.Favorite;
import com.pinyougou.user.service.EvaluateService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import util.IdWorker;

import java.util.Date;
import java.util.List;
@Service
public class EvaluateServiceImpl implements EvaluateService {
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Evaluate evaluate) {
        evaluate.set_id(idWorker.nextId()+"");
        System.out.println(evaluate);
        mongoTemplate.insert(evaluate,"Evals");
    }

    @Override
    public List<Evaluate> findAll(String goodsId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("goodsId").is(goodsId));
        List<Evaluate> all = mongoTemplate.find(query,Evaluate.class,"Evals");
        return all;
    }

    @Override
    public List<Evaluate> findByUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        List<Evaluate> all = mongoTemplate.find(query,Evaluate.class,"Evals");
        return all;
    }

    @Override
    public Evaluate findByOrderId(String orderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("orderId").is(orderId));
        List<Evaluate> all = mongoTemplate.find(query,Evaluate.class,"Evals");
        return all.get(0);
    }

    @Override
    public PageResult findByGoodsId(String goodsId,int page, int size) {
       /* Query query = new Query();
        query.addCriteria(Criteria.where("goodsId").is(goodsId));
        List<Evaluate> all = mongoTemplate.find(query,Evaluate.class,"Evaluate");
        return all;*/

        Query query = Query.query(Criteria.where("goodsId").is(goodsId)).with(new Sort(new Sort.Order(Sort.Direction.DESC,"evaluateTime"))).skip((page - 1) * size).limit(size);
        List<Evaluate> all = mongoTemplate.find(query,Evaluate.class,"Evals");
        int count = (int) mongoTemplate.count(query, Evaluate.class, "Evals");
        return new PageResult(count,all);

    }

    @Override
    public void delete(String id) {
        Query query=new Query(Criteria.where("_id").is(id));
        int favorite = mongoTemplate.remove(query, Evaluate.class, "Evals").getN();
    }
    @Override
    public void deleteAll(String name) {
        Query query=new Query(Criteria.where("username").is(name));
        int favorite = mongoTemplate.remove(query, Evaluate.class, "Evals").getN();
        System.out.println(favorite);
    }

    @Override
    public void update(Evaluate evaluate, String id) {
        delete(id);
        evaluate.set_id(id);
        save(evaluate);
    }


}
