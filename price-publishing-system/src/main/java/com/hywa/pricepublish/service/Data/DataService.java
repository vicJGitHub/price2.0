package com.hywa.pricepublish.service.Data;

import com.hywa.pricepublish.representation.SelfTemplateRep;

import javax.servlet.http.HttpServletResponse;

public interface DataService {

    void downloadSelfTemplateBySell(SelfTemplateRep selfTemplateRep,
                                    HttpServletResponse response) ;

    void downloadSelfTemplateByWholesale(SelfTemplateRep selfTemplateRep,
                                    HttpServletResponse response) ;
}
