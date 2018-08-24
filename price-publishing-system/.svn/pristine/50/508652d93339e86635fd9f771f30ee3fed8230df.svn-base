package com.hywa.pricepublish.service.collect;

import com.hywa.pricepublish.representation.PriceLeaderBoardRep;
import com.hywa.pricepublish.representation.PriceMonitorRep;
import java.util.List;

public interface PriceAnalysisService {

    List<PriceLeaderBoardRep> selectLeaderBoard(String priceTypeId, String productBigTypeId,
            short timeType, short priceChangeType);

    List<PriceMonitorRep> selectPriceMonitor(String priceTypeId, String productBigTypeId,
            short timeType, String cityId, String countyId, short isFocusOn);
}
