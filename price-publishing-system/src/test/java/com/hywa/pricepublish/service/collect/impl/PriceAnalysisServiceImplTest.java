package com.hywa.pricepublish.service.collect.impl;

import com.hywa.pricepublish.common.enums.CommonEnum;
import com.hywa.pricepublish.common.utils.DateUtils;
import com.hywa.pricepublish.dao.entity.AvgPriceStatistics;
import com.hywa.pricepublish.dao.mapper.AvgPriceStatisticsMapper;
import com.hywa.pricepublish.representation.PriceLeaderBoardRep;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PriceAnalysisServiceImplTest {

    private List<AvgPriceStatistics> todayPrice;
    private List<AvgPriceStatistics> yesterdayPrice;

    @Mock
    private AvgPriceStatisticsMapper avgPriceStatisticsMapper;

    @InjectMocks
    private PriceAnalysisServiceImpl priceAnalysisService;

    @Before
    public void setUp() throws ParseException {
        initData();
    }

    private void initData() throws ParseException {
        todayPrice = new ArrayList<>();
        yesterdayPrice = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtils.stringToDate("2018-07-01", DateUtils.DEFAULT_FORMAT));

        for (int i = 0; i < 60; i++) {
            c.add(Calendar.DAY_OF_MONTH, i);
            AvgPriceStatistics priceStatistics = new AvgPriceStatistics();
            priceStatistics.setId(i + "");
            priceStatistics.setProductName("商品" + i);
            priceStatistics.setCountyId(i + "" + i + i);
            priceStatistics.setCityId(i + "" + i);
            priceStatistics.setProvinceId(i + "");
            priceStatistics.setCreateTime(c.getTime());
            priceStatistics.setPriceTypeId(i + "");
            priceStatistics.setProductId(i + "");
            priceStatistics.setUnit("元/斤");
            priceStatistics.setAvgPrice(new BigDecimal(
                    new Random().nextInt(70) == 0 ? (3.2 + i) : new Random().nextInt(70)));

            AvgPriceStatistics yesterday = new AvgPriceStatistics();
            yesterday.setId(i + "");
            yesterday.setProductName("商品" + i);
            yesterday.setCountyId(i + "" + i + i);
            yesterday.setCityId(i + "" + i);
            yesterday.setProvinceId(i + "");
            yesterday.setCreateTime(c.getTime());
            yesterday.setPriceTypeId(i + "");
            yesterday.setProductId(i + "");
            yesterday.setUnit("元/斤");
            yesterday.setAvgPrice(new BigDecimal(
                    new Random().nextInt(60) == 0 ? (4.7 + i) : new Random().nextInt(70)));

            todayPrice.add(priceStatistics);
            yesterdayPrice.add(yesterday);
        }
    }

    @After
    public void tearDown() {

    }

    @Test
    public void selectLeaderBoard() {
        Mockito.when(avgPriceStatisticsMapper.selectThisPeriodPrice("", "", (short) 1))
                .thenReturn(todayPrice);
        Mockito.when(avgPriceStatisticsMapper.selectLastPeriodPrice("", "", (short) 1))
                .thenReturn(yesterdayPrice);
        List<PriceLeaderBoardRep> priceLeaderBoardRepList = priceAnalysisService
                .selectLeaderBoard("", "", (short) 1, CommonEnum.INCREASE.getIndex());

        System.out.println(todayPrice);
        System.out.println(yesterdayPrice);
        System.out.println(priceLeaderBoardRepList);
    }
}