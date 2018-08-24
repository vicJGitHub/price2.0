package com.hywa.pricepublish.controller;

import static com.hywa.pricepublish.common.enums.CommonEnum.FILE_CONTENT_NOT_NULL;
import static com.hywa.pricepublish.common.enums.CommonEnum.FILE_NOT_NULL;
import static com.hywa.pricepublish.common.enums.CommonEnum.SUCCESS;
import com.hywa.pricepublish.common.enums.CommonEnum;
import com.hywa.pricepublish.common.exception.GlobalException;
import com.hywa.pricepublish.common.utils.ExcelUtils;
import com.hywa.pricepublish.common.utils.FileUtils;
import com.hywa.pricepublish.config.EnvProperties;
import com.hywa.pricepublish.config.interceptor.TokenManager;
import com.hywa.pricepublish.config.interceptor.TokenModel;
import com.hywa.pricepublish.dao.entity.File;
import com.hywa.pricepublish.representation.PriceCollectStatisticsRep;
import com.hywa.pricepublish.representation.PriceCollectStatisticsReps;
import com.hywa.pricepublish.representation.PriceCrawlerDataRep;
import com.hywa.pricepublish.representation.PriceCrawlerDataReps;
import com.hywa.pricepublish.representation.ResponseBase;
import com.hywa.pricepublish.service.collect.ArtificialCollectionService;
import com.hywa.pricepublish.service.collect.FileService;
import com.hywa.pricepublish.service.collect.PriceCollectionService;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private EnvProperties envProperties;

    @Autowired
    private ArtificialCollectionService artificialCollectionService;
    
    @Autowired
    private PriceCollectionService priceCollectionService;
    
    @Autowired
    TokenManager tokenManager;

    @RequestMapping(value = "/upload")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        CommonEnum upload = FileUtils.upload(file, envProperties.getPicturePath());
        ResponseBase responseBase = new ResponseBase();
        responseBase.setRetHead(upload.getIndex(), upload.getValue());
        return new ResponseEntity<>(responseBase, HttpStatus.OK);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadFile(@RequestParam String fileName,
            HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException {
        FileUtils.downloadFile(envProperties.getPicturePath(), fileName, response,request);
    }

    @RequestMapping(value = "/showImage", method = RequestMethod.GET)
    public void showImage(@RequestParam String fileName, HttpServletResponse response) {
        FileUtils.showImage(envProperties.getPicturePath()+fileName, response);
    }

    //多文件上传
    @RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
    public ResponseEntity handleFileUpload(HttpServletRequest request) {
        try {
            String msg = FileUtils.handleFileUpload(request);
            ResponseBase responseBase = new ResponseBase();
            responseBase.setRetHead(SUCCESS.getIndex(), msg);
            return new ResponseEntity<>(responseBase, HttpStatus.OK);
        } catch (FileUploadException e) {
            ResponseBase responseBase = new ResponseBase();
            responseBase.setRetHead(CommonEnum.FAILURE.getIndex(), e.getMessage());
            return new ResponseEntity<>(responseBase, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "crawlerData/getExcel", method = RequestMethod.GET)
    public void crawlerDataGetExcel(HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "价格爬虫数据") String tableName,
            @RequestParam(required = false, defaultValue = "价格爬虫数据展示") String tableTitle,
            @RequestParam(required = false, defaultValue = "爬虫数据") String sheetName,
            @RequestParam(required = false) String productNameOrMarketName,
            @RequestParam(required = false) String priceType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        try {
            LinkedHashMap<String, String> filedNames = new LinkedHashMap<>();
            filedNames.put("updateTime", "采集时间");
            filedNames.put("productName", "产品名称");
            filedNames.put("Data", "价格");
            filedNames.put("unit", "计量单位");
            filedNames.put("marketName", "市场名称");
            filedNames.put("specification", "规格");
            filedNames.put("dataSources", "数据源");
            filedNames.put("priceType", "价格类型");
            filedNames.put("province", "省");
            filedNames.put("city", "市");
            filedNames.put("county", "区");      
            List<PriceCrawlerDataRep> list=priceCollectionService.findCrawlerCollectDataAll(
            		productNameOrMarketName,priceType,startTime,endTime);
            ExcelUtils.exportExcel(list,
                    response, tableName, tableTitle, sheetName, filedNames);
        } catch (Exception e) {
        			e.getStackTrace();
        }
    }

    @RequestMapping(value = "collectData/getExcel", method = RequestMethod.GET)
    public void getExcel(HttpServletResponse response,
            @RequestParam(required = false, defaultValue = "采集数据") String tableName,
            @RequestParam(required = false, defaultValue = "采集数据展示") String tableTitle,
            @RequestParam(required = false, defaultValue = "采集数据") String sheetName,
            @RequestParam(required = false) String productNameOrMarketName,
            @RequestParam(required = false) String priceType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        try {
            LinkedHashMap<String, String> filedNames = new LinkedHashMap<>();
            filedNames.put("collectTime", "采集时间");
            filedNames.put("productName", "产品名称");
            filedNames.put("Data", "价格");
            filedNames.put("unit", "计量单位");
            filedNames.put("marketName", "市场名称");
            filedNames.put("collector", "采集人");
            //filedNames.put("dataOrigion", "数据源");
            filedNames.put("priceType", "价格类型");
            filedNames.put("bigTypeName", "产品大类");
            filedNames.put("typeName", "产品小类");
            filedNames.put("province", "省");
            filedNames.put("city", "市");
            filedNames.put("county", "区");
            List<PriceCollectStatisticsRep> list = priceCollectionService.findCollectAll
            		( productNameOrMarketName, priceType, startTime, endTime);    
            ExcelUtils.exportExcel(list,response, tableName, tableTitle, sheetName, filedNames);
        } catch (Exception e) {
        			e.getStackTrace();
        }
    }

    @Transactional
    @PostMapping(value = "artificialCollect/uploadExcel")
    public ResponseEntity uploadArtificialCollectExcel(
            @RequestParam String userId,
            @RequestParam(required = false) String description,
             MultipartFile file) {
    	 log.info("模板大小:"+file.getSize());
        if (file.isEmpty()) {
            throw new GlobalException(FILE_NOT_NULL.getIndex(), FILE_NOT_NULL.getValue());
        }
        List<Map<String, String>> maps = ExcelUtils
                .importExcel(file, envProperties.getPriceCollectFilePath());
        try {
        	priceCollectionService.save(userId,maps);
            //artificialCollectionService.save(maps, userId);
            fileService.save(userId, file.getSize(), file.getOriginalFilename(), description);
        } catch (Exception e) {
            FileUtils.delete(file.getOriginalFilename(), envProperties.getPriceCollectFilePath());
            throw e;
        }
        return new ResponseEntity<>(new ResponseBase().success(), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ResponseEntity delete(@RequestParam String fileId) {
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value="find",method=RequestMethod.GET)
    public ResponseEntity<ResponseBase> find(HttpServletRequest req) {
    	TokenModel accessToken = tokenManager.getToken(req.getHeader("accessToken"));
    	ResponseBase responseBase = new ResponseBase();
        responseBase.success();
        responseBase.setRetBody(fileService.find());
        return new ResponseEntity<>(responseBase, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(@RequestParam String id,
            HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException {
    	File file=fileService.findById(id);
    	if(null==file) {
    		throw new GlobalException(CommonEnum.FILE_CANNOT_BE_FOUND.getIndex(),CommonEnum.FILE_CANNOT_BE_FOUND.getValue());
    	}
        FileUtils.downloadFile(file.getPath(), file.getName(), response,request);
    }
}
