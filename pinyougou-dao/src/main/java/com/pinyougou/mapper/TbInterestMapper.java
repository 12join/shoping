package com.pinyougou.mapper;

import com.pinyougou.pojo.TbInterest;
import com.pinyougou.pojo.TbInterestExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbInterestMapper {
    int countByExample(TbInterestExample example);

    int deleteByExample(TbInterestExample example);

    int deleteByPrimaryKey(String id);

    int insert(TbInterest record);

    int insertSelective(TbInterest record);

    List<TbInterest> selectByExample(TbInterestExample example);

    TbInterest selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") TbInterest record, @Param("example") TbInterestExample example);

    int updateByExample(@Param("record") TbInterest record, @Param("example") TbInterestExample example);

    int updateByPrimaryKeySelective(TbInterest record);

    int updateByPrimaryKey(TbInterest record);
}