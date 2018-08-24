package com.hywa.pricepublish.service.collect.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hywa.pricepublish.common.ConstantPool;
import com.hywa.pricepublish.common.utils.UUIDUtils;
import com.hywa.pricepublish.dao.entity.Market;
import com.hywa.pricepublish.dao.entity.MarketRecentUse;
import com.hywa.pricepublish.dao.entity.MarketRecentUseExample;
import com.hywa.pricepublish.dao.entity.entityExt.MarketExt;
import com.hywa.pricepublish.dao.mapper.MarketMapper;
import com.hywa.pricepublish.dao.mapper.MarketRecentUseMapper;
import com.hywa.pricepublish.representation.MarketRecentUseRep;
import com.hywa.pricepublish.representation.MarketRecentUseReps;
import com.hywa.pricepublish.representation.MarketRep;
import com.hywa.pricepublish.representation.MarketsReps;
import com.hywa.pricepublish.service.collect.MarketService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MarketServiceImpl implements MarketService {

    @Autowired
    private MarketMapper marketMapper;

    @Autowired
    private MarketRecentUseMapper marketRecentUseMapper;

    @Override
    public MarketsReps findMarkets(String marketType,String id,String marketTypeId, String marketName, String regionId,
            Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, true);
        List<MarketExt> markets = marketMapper.findMarkets(marketType,id,marketTypeId, marketName, regionId);
       /* List<MarketRep> marketReps=new ArrayList<>();
        markets.forEach(m->{
        	new MarketRep();
        });*/
        return new MarketsReps(new PageInfo<>(markets).getTotal(), markets);
    }

    @Override
    public void save(MarketRep marketRep, String userId) {
        Market market = new Market();
        market.setCreateTime(new Date());
        market.setIsDel(ConstantPool.NOT_DEL);
        market.setCreateUser(userId);
        market.setName(marketRep.getMarketName());
        market.setUpdateTime(new Date());
        market.setId(UUIDUtils.randomUUID());
        market.setMarketType(marketRep.getMarketType());
        market.setPriceType(marketRep.getPriceType());
        market.setProvinceId(marketRep.getProvinceId());
        market.setCityId(marketRep.getCityId());
        market.setCountyId(marketRep.getCountyId());
        market.setTownId(marketRep.getTown());
        market.setVillageId(marketRep.getVillage());
        market.setRegionId(marketRep.getCountyId());
        market.setRegionType(getRegionType(marketRep.getVillage(), marketRep.getTown()));
        marketMapper.insert(market);
    }

    @Override
    public void update(MarketRep marketRep, String userId) {
        Market market = new Market();
        market.setId(marketRep.getMarketId());
        market.setUpdateUser(userId);
        market.setName(marketRep.getMarketName());
        market.setUpdateTime(new Date());
        market.setMarketType(marketRep.getMarketType());
        market.setPriceType(marketRep.getPriceType());
        market.setProvinceId(marketRep.getProvinceId());
        market.setCityId(marketRep.getCityId());
        market.setCountyId(marketRep.getCountyId());
        market.setTownId(marketRep.getTown());
        market.setVillageId(marketRep.getVillage());
        market.setRegionId(marketRep.getCountyId());
        market.setRegionType(getRegionType(marketRep.getVillage(), marketRep.getTown()));
        marketMapper.updateByPrimaryKey(market);
    }

    short getRegionType(String villageId, String townId) {
        if (villageId != null) {
            return ConstantPool.REGION_VILLAGE;
        } else if (townId != null) {
            return ConstantPool.REGION_TOWN;
        } else {
            return ConstantPool.REGION_COUNTY;
        }
    }

    @Override
    public MarketRecentUseReps findMarketRecentUse(String userId) {
        List<MarketRecentUseRep> marketRecentUseReps = new ArrayList<>();
        MarketRecentUseExample example = new MarketRecentUseExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<MarketRecentUse> marketRecentUses = marketRecentUseMapper.selectByExample(example);
        marketRecentUses.forEach(marketRecentUse -> {
            String marketId = marketRecentUse.getMarketId();
            String marketName = marketMapper.selectNameById(marketId);
            marketRecentUseReps.add(new MarketRecentUseRep(marketId, marketName));
        });
        return new MarketRecentUseReps(marketRecentUseReps);
    }

    @Override
    public void deleteById(String marketId) {
        marketMapper.deleteByPrimaryKey(marketId);
    }
}
