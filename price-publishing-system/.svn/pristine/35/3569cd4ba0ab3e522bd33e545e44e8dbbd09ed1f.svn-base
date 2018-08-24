package com.hywa.pricepublish.dao.mapper;

import com.hywa.pricepublish.dao.entity.AvgPriceStatistics;
import com.hywa.pricepublish.dao.entity.AvgPriceStatisticsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AvgPriceStatisticsMapper {

    int countByExample(AvgPriceStatisticsExample example);

    int deleteByExample(AvgPriceStatisticsExample example);

    int deleteByPrimaryKey(String id);

    int insert(AvgPriceStatistics record);

    int insertSelective(AvgPriceStatistics record);

    List<AvgPriceStatistics> selectByExample(AvgPriceStatisticsExample example);

    AvgPriceStatistics selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") AvgPriceStatistics record,
            @Param("example") AvgPriceStatisticsExample example);

    int updateByExample(@Param("record") AvgPriceStatistics record,
            @Param("example") AvgPriceStatisticsExample example);

    int updateByPrimaryKeySelective(AvgPriceStatistics record);

    int updateByPrimaryKey(AvgPriceStatistics record);

    List<AvgPriceStatistics> selectThisPeriodPrice(@Param("priceTypeId") String priceTypeId,
            @Param("productBigTypeId") String productBigTypeId, @Param("timeType") Short timeType);

    List<AvgPriceStatistics> selectLastPeriodPrice(@Param("priceTypeId") String priceTypeId,
            @Param("productBigTypeId") String productBigTypeId, @Param("timeType") Short timeType);
}