package com.hywa.pricepublish.controller.Data;

import com.hywa.pricepublish.representation.SelfTemplateRep;
import com.hywa.pricepublish.service.Data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 数据导入
 */
@RestController
@RequestMapping("data")
public class DataController {

    @Autowired
    DataService dataService;

    //(销售模板)
    @PostMapping("downloadSelfTemplateBySell")
    public void downloadSelfTemplateBySell(@RequestBody SelfTemplateRep selfTemplateRep,
                                           HttpServletResponse response) {
        dataService.downloadSelfTemplateBySell(selfTemplateRep,response);
    }

    //(批发模板)
    @PostMapping("downloadSelfTemplateByWholesale")
    public void downloadSelfTemplateByWholesale(@RequestBody SelfTemplateRep selfTemplateRep,
                                           HttpServletResponse response) {
        dataService.downloadSelfTemplateByWholesale(selfTemplateRep,response);
    }


}
