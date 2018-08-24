package com.hywa.pricepublish.service.collect.impl;

import static com.hywa.pricepublish.common.enums.CommonEnum.FALLING;
import static com.hywa.pricepublish.common.enums.CommonEnum.PERCENT_TO_DOUBLE_ERROR;

import com.hywa.pricepublish.common.exception.GlobalException;
import com.hywa.pricepublish.dao.entity.AvgPriceStatistics;
import com.hywa.pricepublish.dao.mapper.AvgPriceStatisticsMapper;
import com.hywa.pricepublish.representation.PriceLeaderBoardRep;
import com.hywa.pricepublish.representation.PriceMonitorRep;
import com.hywa.pricepublish.service.collect.PriceAnalysisService;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceAnalysisServiceImpl implements PriceAnalysisService {

    private static final short WEEK = 2;
    private static final short MONTH = 3;
    private static final short QUARTER = 4;
    private static final short YEAR = 5;

    @Autowired
    AvgPriceStatisticsMapper avgPriceStatisticsMapper;

    @Override
    public List<PriceLeaderBoardRep> selectLeaderBoard(String priceTypeId, String productBigTypeId,
            short timeType, short priceChangeType) {

        List<AvgPriceStatistics> currentPrices = avgPriceStatisticsMapper
                .selectThisPeriodPrice(priceTypeId, productBigTypeId, timeType);

        List<AvgPriceStatistics> lastPeriodPrices = avgPriceStatisticsMapper
                .selectLastPeriodPrice(priceTypeId, productBigTypeId, timeType);

        List<PriceLeaderBoardRep> priceLeaderBoardRepList = new ArrayList<>();
        for (AvgPriceStatistics currentPrice : currentPrices) {
            for (AvgPriceStatistics lastPeriodPrice : lastPeriodPrices) {
                if (currentPrice.getProductId().equals(lastPeriodPrice.getProductId()) &&
                        currentPrice.getMarketId().equals(lastPeriodPrice.getMarketId())) {
                    BigDecimal currentAvgPrice = currentPrice.getAvgPrice();
                    BigDecimal lastPeriodAvgPrice = lastPeriodPrice.getAvgPrice();
                    PriceLeaderBoardRep priceLeaderBoardRep = new PriceLeaderBoardRep();
                    priceLeaderBoardRep.setCurrentPrice(currentAvgPrice);
                    priceLeaderBoardRep.setLastTermPrice(lastPeriodAvgPrice);
                    priceLeaderBoardRep.setProductName(currentPrice.getProductName());
                    priceLeaderBoardRep.setUnit(lastPeriodPrice.getUnit());
                    priceLeaderBoardRep.setAmplitude(
                            calculationIncrease(currentAvgPrice.doubleValue(),
                                    lastPeriodAvgPrice.doubleValue()));
                    priceLeaderBoardRepList.add(priceLeaderBoardRep);
                }
            }
        }

        return priceLeaderBoardRepList.stream()
                .filter(priceLeaderBoardRep ->
                        (priceChangeType == FALLING.getIndex()) == priceLeaderBoardRep
                                .getAmplitude().contains("-"))
                .sorted((o1, o2) -> format(o1.getAmplitude()) - format(o2.getAmplitude()) > 0 ? 1
                        : (format(o1.getAmplitude()) - format(o2.getAmplitude()) == 0 ? 0 : -1))
                .limit(20)
                .collect(Collectors.toList());
    }

    private double format(String amplitude) {
        try {
            NumberFormat nf = NumberFormat.getPercentInstance();
            Number m = nf.parse(amplitude);
            return m.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new GlobalException(PERCENT_TO_DOUBLE_ERROR.getIndex(),
                    PERCENT_TO_DOUBLE_ERROR.getValue());
        }
    }

    private String calculationIncrease(double currentPrice, double lastTermPrice) {
        double amplitude = (currentPrice - lastTermPrice) / lastTermPrice;
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);
        return nt.format(amplitude);
    }


    @Override
    public List<PriceMonitorRep> selectPriceMonitor(String priceTypeId, String productBigTypeId,
            short timeType, String cityId, String countyId, short isFocusOn) {

        //TODO 查询条件判断及使用条件
        List<AvgPriceStatistics> currentPrices = avgPriceStatisticsMapper
                .selectThisPeriodPrice(priceTypeId, productBigTypeId, timeType);

        List<AvgPriceStatistics> lastWeekAvgPrices = null;
        if (timeType != MONTH && timeType != YEAR && timeType != QUARTER) {
            lastWeekAvgPrices = avgPriceStatisticsMapper
                    .selectLastPeriodPrice(priceTypeId, productBigTypeId, WEEK);
        }

        List<AvgPriceStatistics> lastMonthAvgPrices = avgPriceStatisticsMapper
                .selectLastPeriodPrice(priceTypeId, productBigTypeId, MONTH);
        List<AvgPriceStatistics> lastYearAvgPrices = avgPriceStatisticsMapper
                .selectLastPeriodPrice(priceTypeId, productBigTypeId, YEAR);

        List<PriceMonitorRep> priceMonitorRepList = new ArrayList<>();
        for (AvgPriceStatistics currentPrice : currentPrices) {
            PriceMonitorRep priceMonitorRep = new PriceMonitorRep();
            priceMonitorRep.setUnit(currentPrice.getUnit());
            priceMonitorRep.setProductName(currentPrice.getProductName());
            priceMonitorRep.setCurrentPrice(currentPrice.getAvgPrice());

            if (lastWeekAvgPrices != null) {
                setContrast(lastWeekAvgPrices, currentPrice, priceMonitorRep, WEEK);
            }
            setContrast(lastMonthAvgPrices, currentPrice, priceMonitorRep, MONTH);
            setContrast(lastYearAvgPrices, currentPrice, priceMonitorRep, YEAR);
            priceMonitorRepList.add(priceMonitorRep);
        }

        return priceMonitorRepList;
    }

    private void setContrast(List<AvgPriceStatistics> avgPrices,
            AvgPriceStatistics currentPrice, PriceMonitorRep priceMonitorRep, short timeType) {
        for (AvgPriceStatistics lastAvgPrice : avgPrices) {
            if (currentPrice.getProductId().equals(lastAvgPrice.getProductId()) ||
                    currentPrice.getMarketId().equals(lastAvgPrice.getMarketId())) {
                String avgPriceContrast = calculationIncrease(
                        currentPrice.getAvgPrice().doubleValue(),
                        lastAvgPrice.getAvgPrice().doubleValue());
                if (timeType == YEAR) {
                    priceMonitorRep.setLastYearAvgPriceContrast(avgPriceContrast);
                } else if (timeType == MONTH) {
                    priceMonitorRep.setLastMonthAvgPriceContrast(avgPriceContrast);
                } else if (timeType == WEEK) {
                    priceMonitorRep.setLastWeekAvgPriceContrast(avgPriceContrast);
                }
            }
        }
    }
}
