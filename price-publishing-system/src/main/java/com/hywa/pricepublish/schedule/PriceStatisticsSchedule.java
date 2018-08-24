package com.hywa.pricepublish.schedule;

import com.hywa.pricepublish.common.utils.UUIDUtils;
import com.hywa.pricepublish.dao.entity.AvgPriceStatistics;
import com.hywa.pricepublish.dao.entity.County;
import com.hywa.pricepublish.dao.entity.Dict;
import com.hywa.pricepublish.dao.entity.PriceCollection;
import com.hywa.pricepublish.dao.mapper.AvgPriceStatisticsMapper;
import com.hywa.pricepublish.dao.mapper.CountyMapper;
import com.hywa.pricepublish.dao.mapper.DictMapper;
import com.hywa.pricepublish.dao.mapper.PriceCollectionMapper;
import com.hywa.pricepublish.dao.mapper.ProvinceMapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceStatisticsSchedule {

    private PriceCollectionMapper priceCollectionMapper;
    private CountyMapper countyMapper;
    private ProvinceMapper provinceMapper;
    private DictMapper dictMapper;
    private AvgPriceStatisticsMapper avgPriceStatisticsMapper;

    @Scheduled
    public void calculateTodayAveragePrice() {
        String provinceId = provinceMapper.getIdByName("贵州省");
        List<County> counties = countyMapper.getCountiesByProvinceId(provinceId);
        List<Dict> dicts = dictMapper.selectPriceType();
        for (County county : counties) {
            for (Dict dict : dicts) {
                List<PriceCollection> priceCollections = priceCollectionMapper
                        .calculateTodayAveragePriceByPriceTypeAndCounty(dict.getId(),
                                county.getId());

                if (priceCollections.size() > 0) {
                    savePriceStatistics(provinceId, county, dict, priceCollections);
                }
            }
        }
    }

    private void savePriceStatistics(String provinceId, County county, Dict dict,
            List<PriceCollection> priceCollections) {
        List<AvgPriceStatistics> list = new ArrayList<>();
        for (PriceCollection priceCollection : priceCollections) {
            AvgPriceStatistics priceStatistics = new AvgPriceStatistics();
            priceStatistics.setId(UUIDUtils.randomUUID());
            priceStatistics.setProductId(priceCollection.getProductId());
            priceStatistics.setAvgPrice(priceCollection.getPrice());
            priceStatistics.setUnit(priceCollection.getUnit());
            priceStatistics.setPriceTypeId(dict.getId());
            priceStatistics
                    .setCreateTime(new Date());
            priceStatistics.setProvinceId(provinceId);
            priceStatistics.setCityId(county.getCityId());
            priceStatistics.setCountyId(county.getId());
            list.add(priceStatistics);
        }
//        avgPriceStatisticsMapper.insertBatch(list);
    }
}
