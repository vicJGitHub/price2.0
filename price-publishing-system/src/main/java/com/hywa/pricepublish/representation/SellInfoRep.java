package com.hywa.pricepublish.representation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 销售模板信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellInfoRep {
    private String date;
    private String productName;
    private String price;
    private String unit;
    private String priceType;
    private String TypeName;
    private String marketName;
    private String province;
    private String city;
    private String county;
    private String collector;
    private String collectTime;
}
