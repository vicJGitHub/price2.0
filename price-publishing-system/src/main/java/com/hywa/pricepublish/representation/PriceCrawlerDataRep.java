package com.hywa.pricepublish.representation;

import com.hywa.pricepublish.dao.entity.PriceCrawlerData;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PriceCrawlerDataRep {

    Long id;
    String productName;
    String price;
    String unit;
    String marketName;
    String province;
    String city;
    String county;
    String updateTime;
    String specification;
    String dataSources;
    String priceType;

    public PriceCrawlerDataRep(PriceCrawlerData priceCrawlerData) {
        this.setId(priceCrawlerData.getId());
        this.setDataSources(priceCrawlerData.getDataOrigion());
        this.setProductName(priceCrawlerData.getName());
        this.setPrice(priceCrawlerData.getPrice());
        this.setUnit("元/"+priceCrawlerData.getPriceUnit());
        this.setMarketName(priceCrawlerData.getMarket());
        this.setUpdateTime(priceCrawlerData.getUpdateTime());
        this.setSpecification(priceCrawlerData.getSpecification());
        this.setPriceType(priceCrawlerData.getPriceType());
        this.setProvince(priceCrawlerData.getProvince());
        this.setCity(priceCrawlerData.getCity());
        this.setCounty(priceCrawlerData.getCounty());      
    }
}
