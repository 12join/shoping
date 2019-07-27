package com.pinyougou.user.service.impl;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbAreasMapper;
import com.pinyougou.mapper.TbCitiesMapper;
import com.pinyougou.mapper.TbProvincesMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddressExample.Criteria;
import com.pinyougou.user.service.AddressService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private TbAddressMapper addressMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbAddress> findAll() {
		return addressMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbAddress> page=   (Page<TbAddress>) addressMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

    /**
     * 增加
     */
    @Override
    public void add(String userId,TbAddress address) {
        address.setUserId(userId);
        address.setCreateDate(new Date());
        if("1".equals(address.getIsDefault())) {
            updateAddressDefault(userId);
        }
        addressMapper.insert(address);
    }

    private void updateAddressDefault(String userId) {
        TbAddress tbAddress = new TbAddress();
        tbAddress.setIsDefault("0");
        TbAddressExample example = new TbAddressExample();
        Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        addressMapper.updateByExampleSelective(tbAddress, example);
    }

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbAddress address){
        if("1".equals(address.getIsDefault())) {
            updateAddressDefault(address.getUserId());
        }
        addressMapper.updateByPrimaryKey(address);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbAddress findOne(Long id){
		return addressMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			addressMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbAddress address, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbAddressExample example=new TbAddressExample();
		Criteria criteria = example.createCriteria();
		
		if(address!=null){			
						if(address.getUserId()!=null && address.getUserId().length()>0){
				criteria.andUserIdLike("%"+address.getUserId()+"%");
			}
			if(address.getProvinceId()!=null && address.getProvinceId().length()>0){
				criteria.andProvinceIdLike("%"+address.getProvinceId()+"%");
			}
			if(address.getCityId()!=null && address.getCityId().length()>0){
				criteria.andCityIdLike("%"+address.getCityId()+"%");
			}
			if(address.getTownId()!=null && address.getTownId().length()>0){
				criteria.andTownIdLike("%"+address.getTownId()+"%");
			}
			if(address.getMobile()!=null && address.getMobile().length()>0){
				criteria.andMobileLike("%"+address.getMobile()+"%");
			}
			if(address.getAddress()!=null && address.getAddress().length()>0){
				criteria.andAddressLike("%"+address.getAddress()+"%");
			}
			if(address.getContact()!=null && address.getContact().length()>0){
				criteria.andContactLike("%"+address.getContact()+"%");
			}
			if(address.getIsDefault()!=null && address.getIsDefault().length()>0){
				criteria.andIsDefaultLike("%"+address.getIsDefault()+"%");
			}
			if(address.getNotes()!=null && address.getNotes().length()>0){
				criteria.andNotesLike("%"+address.getNotes()+"%");
			}
			if(address.getAlias()!=null && address.getAlias().length()>0){
				criteria.andAliasLike("%"+address.getAlias()+"%");
			}
	
		}
		
		Page<TbAddress> page= (Page<TbAddress>)addressMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbAddress> findListByUserId(String userId) {
        try {
            TbAddressExample example = new TbAddressExample();
            Criteria criteria = example.createCriteria();
            criteria.andUserIdEqualTo(userId);
            return addressMapper.selectByExample(example);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Autowired
    private TbProvincesMapper tbProvincesMapper;

    @Override
    public List<TbProvinces> findProvince() {
        return tbProvincesMapper.selectByExample(null);
    }


    @Autowired
    private TbCitiesMapper tbCitiesMapper;


    @Override
    public List<TbCities> findCity(String id) {
        TbCitiesExample example = new TbCitiesExample();
        TbCitiesExample.Criteria criteria = example.createCriteria();
        criteria.andProvinceidEqualTo(id);
        return tbCitiesMapper.selectByExample(example);
    }


    @Autowired
    private TbAreasMapper tbAreasMapper;

    @Override
    public List<TbAreas> findAreas(String id) {
        TbAreasExample example = new TbAreasExample();
        TbAreasExample.Criteria criteria = example.createCriteria();
        criteria.andCityidEqualTo(id);
        return tbAreasMapper.selectByExample(example);
    }

    @Override
    public TbProvinces findProvinceById(String provinceId) {
        TbProvincesExample example = new TbProvincesExample();
        TbProvincesExample.Criteria criteria = example.createCriteria();
        criteria.andProvinceidEqualTo(provinceId);
        return tbProvincesMapper.selectByExample(example).get(0);
    }

    @Override
    public TbCities findCitiesById(String cityId) {
        TbCitiesExample example = new TbCitiesExample();
        TbCitiesExample.Criteria criteria = example.createCriteria();
        criteria.andCityidEqualTo(cityId);
        return tbCitiesMapper.selectByExample(example).get(0);
    }

    @Override
    public TbAreas findAreasById(String areaId) {
        TbAreasExample example = new TbAreasExample();
        TbAreasExample.Criteria criteria = example.createCriteria();
        criteria.andAreaidEqualTo(areaId);
        return tbAreasMapper.selectByExample(example).get(0);
    }

}
