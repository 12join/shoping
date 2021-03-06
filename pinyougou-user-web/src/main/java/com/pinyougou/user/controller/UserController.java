package com.pinyougou.user.controller;
import java.util.List;

import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;


import com.pinyougou.pojo.TbInterest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;

import entity.PageResult;
import entity.Result;
import util.PhoneFormatCheckUtils;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference(timeout =10000)
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String smscode){
		
		//校验验证码是否正确
		boolean checkSmsCode = userService.checkSmsCode(user.getPhone(), smscode);
		if(!checkSmsCode){
			return new Result(false, "验证码不正确！");
		}
		
		
		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
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
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}
	
	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		
		if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
			return new Result(false, "手机格式不正确");
		}
		
		try {
			userService.createSmsCode(phone);
			return new Result(true, "验证码发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "验证码发送失败");
		}
	}


	@RequestMapping("/findUser")
    public TbUser findUser(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        try {
            TbUser user = userService.findUser(name);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @RequestMapping("/updateUserInfo")
    public Result add(@RequestBody TbUser user){
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.updateUserInfo(user);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "系统错误,请稍后再试");
        }
    }

    @RequestMapping("/findInterest")
    public List<TbInterest> findProvince(){

        return userService.findInterest();
    }


    @Reference(timeout = 1000000)
    private OrderService orderService;

    @RequestMapping("/sendMessage")
    public Result sendMessage(@RequestBody TbOrder order){

        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            orderService.sendMessage(order,userName);
            return new Result(true, "发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "发送失败");
        }
    }

}
