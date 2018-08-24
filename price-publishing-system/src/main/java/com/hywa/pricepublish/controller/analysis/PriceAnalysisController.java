package com.hywa.pricepublish.controller.analysis;

import com.hywa.pricepublish.representation.PriceLeaderBoardRep;
import com.hywa.pricepublish.representation.PriceLeaderBoardReps;
import com.hywa.pricepublish.representation.PriceMonitorRep;
import com.hywa.pricepublish.representation.PriceMonitorReps;
import com.hywa.pricepublish.representation.ResponseBase;
import com.hywa.pricepublish.service.collect.PriceAnalysisService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/price/analysis")
public class PriceAnalysisController {

    @Autowired
    PriceAnalysisService priceAnalysisService;

    @RequestMapping(value = "/leaderBoard", method = RequestMethod.GET)
    public ResponseEntity<ResponseBase> leaderBoard(
            @RequestParam(required = false) String priceTypeId,
            @RequestParam(required = false) String productBigTypeId,
            short timeType, short priceChangeType) {

        ResponseBase<PriceLeaderBoardReps> repsResponseBase = new ResponseBase<>();
        List<PriceLeaderBoardRep> priceLeaderBoards = priceAnalysisService
                .selectLeaderBoard(priceTypeId, productBigTypeId, timeType, priceChangeType);

        repsResponseBase.success();
        repsResponseBase.setRetBody(new PriceLeaderBoardReps(priceLeaderBoards));

        return new ResponseEntity<>(repsResponseBase, HttpStatus.OK);
    }


    @RequestMapping(value = "/monitor", method = RequestMethod.GET)
    public ResponseEntity<ResponseBase> leaderBoard(
            @RequestParam(required = false) String priceTypeId,
            @RequestParam(required = false) String productBigTypeId,
            @RequestParam(required = false) String cityId,
            @RequestParam(required = false) String countyId,
            @RequestParam(required = false) short isFocusOn,
            short timeType) {

        ResponseBase<PriceMonitorReps> repsResponseBase = new ResponseBase<>();
        List<PriceMonitorRep> priceMonitorReps = priceAnalysisService
                .selectPriceMonitor(priceTypeId, productBigTypeId, timeType, cityId, countyId,
                        isFocusOn);

        repsResponseBase.success();
        repsResponseBase.setRetBody(new PriceMonitorReps(priceMonitorReps));

        return new ResponseEntity<>(repsResponseBase, HttpStatus.OK);
    }

}
