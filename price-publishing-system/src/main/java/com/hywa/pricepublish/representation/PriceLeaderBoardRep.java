package com.hywa.pricepublish.representation;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PriceLeaderBoardRep {

    private BigDecimal currentPrice;
    private BigDecimal lastTermPrice;
    private String unit;
    private String productName;
    private String amplitude;

}
