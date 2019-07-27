package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.user.service.AddressService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;


    /**
     * 增加
     * @param address
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbAddress address){
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            List<TbAddress> addresses = addressService.findListByUserId(userId);
            if(4==addresses.size()){
                return new Result(false, "地址数量已达上限,无法添加");
            }
            addressService.add(userId,address);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    /**
     * 修改
     * @param address
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbAddress address){
        try {
            addressService.update(address);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbAddress findOne(Long id){
        return addressService.findOne(id);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long [] ids){
        try {
            TbAddress address = addressService.findOne(ids[0]);
            if("1".equals(address.getIsDefault())){
                return new Result(false, "该地址是默认地址,不能删除");
            }
            addressService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    @RequestMapping("/findListByLoginUser")
    public List findListByLoginUser(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TbAddress> addresses = addressService.findListByUserId(userId);
        System.out.println(addresses.size());
        List list = new ArrayList();
        for (TbAddress address : addresses) {
            Map map = new HashMap();
            String province = addressService.findProvinceById(address.getProvinceId()).getProvince();
            String city = addressService.findCitiesById(address.getCityId()).getCity();
            String area = addressService.findAreasById(address.getTownId()).getArea();
            map.put("addStr",province+city+area);
            map.put("address",address);
            list.add(map);
        }
        return list;
    }


    @RequestMapping("/findProvince")
    public List<TbProvinces> findProvince(){

        return addressService.findProvince();
    }


    @RequestMapping("/findCity")
    public List<TbCities> findCity(String id){

        return addressService.findCity(id);
    }


    @RequestMapping("/findAreas")
    public List<TbAreas> findAreas(String id){

        return addressService.findAreas(id);
    }
}
