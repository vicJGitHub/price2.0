package com.hywa.pricepublish.dao.mapper;

import com.hywa.pricepublish.dao.entity.ContentInfo;
import com.hywa.pricepublish.dao.entity.ContentInfoExample;
import com.hywa.pricepublish.representation.ContentInfoRep;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContentInfoMapper {
    int countByExample(ContentInfoExample example);

    int deleteByExample(ContentInfoExample example);

    int deleteByPrimaryKey(String id);

    int insert(ContentInfo record);

    int insertSelective(ContentInfo record);

    List<ContentInfo> selectByExample(ContentInfoExample example);

    ContentInfo selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ContentInfo record, @Param("example") ContentInfoExample example);

    int updateByExample(@Param("record") ContentInfo record, @Param("example") ContentInfoExample example);

    int updateByPrimaryKeySelective(ContentInfo record);

    int updateByPrimaryKey(ContentInfo record);

    ContentInfoRep findContentById(@Param("id")String id);

    void browsing(@Param("id")String id);
}