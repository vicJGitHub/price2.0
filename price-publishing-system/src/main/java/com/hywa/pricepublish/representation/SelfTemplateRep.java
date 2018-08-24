package com.hywa.pricepublish.representation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfTemplateRep {

    /**市场id*/
    private String marketId;

    /**商品id*/
    private List<String> productIds;
}
