package com.hywa.pricepublish.service.Data.impl;

import com.hywa.pricepublish.common.utils.ExcelUtils;
import com.hywa.pricepublish.dao.mapper.ProductMapper;
import com.hywa.pricepublish.representation.SelfTemplateRep;
import com.hywa.pricepublish.representation.SellInfoRep;
import com.hywa.pricepublish.service.Data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    ProductMapper productMapper;

    @Override
    public void downloadSelfTemplateBySell(SelfTemplateRep selfTemplateRep, HttpServletResponse response) {
            LinkedHashMap<String, String> filedNames = new LinkedHashMap<>();
            filedNames.put("date", "日期");
            filedNames.put("productName", "产品名称");
            filedNames.put("price", "价格");
            filedNames.put("unit", "计量单位");
            filedNames.put("priceType", "价格类型");
            filedNames.put("TypeName", "品类");
            filedNames.put("marketName", "市场名称");
            filedNames.put("province", "省");
            filedNames.put("city", "市");
            filedNames.put("county", "区");
            filedNames.put("collector", "采集人");
            filedNames.put("collectTime", "采集时间");
        List<SellInfoRep> sellInfo = productMapper.findSellInfo(selfTemplateRep);
        ExcelUtils.exportExcel(sellInfo, response, "", "模板", "模板", filedNames);
    }

    @Override
    public void downloadSelfTemplateByWholesale(SelfTemplateRep selfTemplateRep, HttpServletResponse response) {
        LinkedHashMap<String, String> filedNames = new LinkedHashMap<>();
        filedNames.put("date", "日期");
        filedNames.put("productName", "产品名称");
        filedNames.put("price", "价格");
        filedNames.put("unit", "计量单位");
        filedNames.put("priceType", "价格类型");
        filedNames.put("sales", "销量(斤)");
        filedNames.put("marketName", "市场名称");
        filedNames.put("origion", "来源地");
        filedNames.put("collector", "采集人");
        filedNames.put("collectTime", "采集时间");
        List<SellInfoRep> sellInfo = new ArrayList<>();
        ExcelUtils.exportExcel(sellInfo, response, "", "模板", "模板", filedNames);
    }
}
