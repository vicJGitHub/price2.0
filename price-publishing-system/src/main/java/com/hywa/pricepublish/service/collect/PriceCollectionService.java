package com.hywa.pricepublish.service.collect;

import com.hywa.pricepublish.representation.PriceCollectStatisticsRep;
import com.hywa.pricepublish.representation.PriceCollectStatisticsReps;
import com.hywa.pricepublish.representation.PriceCollectionReps;
import com.hywa.pricepublish.representation.PriceCrawlerDataRep;
import com.hywa.pricepublish.representation.ProductRep;
import com.hywa.pricepublish.representation.PriceCrawlerDataReps;
import java.util.List;
import java.util.Map;

public interface PriceCollectionService {

    void save(String userId, String dateTime, String marketId, String marketName,
            List<ProductRep> reps);
    
    void save(String userId, List<Map<String, String>> maps);

    PriceCollectionReps findCollect(String priceCollectId);

    PriceCollectStatisticsReps findCollect(
            int pageNum, int pageSize, String productNameOrMarketName,
            String priceType, String startTime, String endTime);

    void updateCollects(String historyId, PriceCollectionReps priceCollectionReps);

    PriceCrawlerDataReps findCrawlerCollectData(
            int pageNum, int pageSize, String productNameOrMarketName,
            String priceType, String startTime, String endTime);
    
    List<PriceCollectStatisticsRep> findCollectAll(String productNameOrMarketName,
            String priceType, String startTime, String endTime);
    
    public List<PriceCrawlerDataRep> findCrawlerCollectDataAll(
    		String productNameOrMarketName, String priceType, String startTime, String endTime);
    
}
