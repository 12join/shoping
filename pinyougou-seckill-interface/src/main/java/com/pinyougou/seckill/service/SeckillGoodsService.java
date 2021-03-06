package com.pinyougou.seckill.service;
import java.util.List;
import com.pinyougou.pojo.TbSeckillGoods;
import entity.PageResult;


/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillGoods seckillGoods);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillGoods seckillGoods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillGoods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize);
	
	
	/**
	 * 从redis中根据id查出秒杀商品详细信息
	 * @param id
	 * @return
	 */
	public TbSeckillGoods findOneFromRedis(Long id);

    /**
     * 批量修改状态
     * @param ids
     * @param status
     */
    public void updateStatus(Long[] ids, String status);
	
}
