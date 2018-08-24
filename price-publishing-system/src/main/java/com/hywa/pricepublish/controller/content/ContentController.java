package com.hywa.pricepublish.controller.content;

import com.hywa.pricepublish.common.ConstantPool;
import com.hywa.pricepublish.common.utils.FileUtils;
import com.hywa.pricepublish.config.EnvProperties;
import com.hywa.pricepublish.config.interceptor.TokenManager;
import com.hywa.pricepublish.config.interceptor.TokenModel;
import com.hywa.pricepublish.representation.ContentInfoRep;
import com.hywa.pricepublish.representation.ResponseBase;
import com.hywa.pricepublish.service.content.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@RequestMapping("content")
@RestController
public class ContentController {

    @Autowired
    ContentService contentService;

    @Autowired
    TokenManager tokenManager;

    @Autowired
    EnvProperties envProperties;

    @PostMapping
    public ResponseEntity saveOrUpdate(@RequestBody @Valid ContentInfoRep contentInfoRep, HttpServletRequest req) {
        contentInfoRep.setIsDel(ConstantPool.NOT_DEL);
        contentInfoRep.setBrowsingNum(0);
        if (StringUtils.isEmpty(contentInfoRep.getId())) {
            contentInfoRep.setCreateUser(getUserId(req));
            contentInfoRep.setCreateTime(new Date());
            contentService.save(contentInfoRep);
        } else {
            contentInfoRep.setUpdateUser(getUserId(req));
            contentInfoRep.setUpdateTime(new Date());
            contentService.update(contentInfoRep);
        }
        ResponseBase responseBase = new ResponseBase();
        return new ResponseEntity(responseBase.success(), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity del(@RequestBody ContentInfoRep contentInfoRep, HttpServletRequest req) {
        contentInfoRep.setUpdateTime(new Date());
        contentInfoRep.setUpdateUser(getUserId(req));
        contentInfoRep.setIsDel(ConstantPool.DEL);
        contentService.update(contentInfoRep);
        ResponseBase responseBase = new ResponseBase();
        return new ResponseEntity(responseBase.success(), HttpStatus.OK);
    }

    @GetMapping("findContentInfoAll")
    public ResponseEntity findArticleInfoAll(@RequestParam(defaultValue = "1") int pageNum,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             ContentInfoRep contentInfoRep) {
        ResponseBase responseBase = new ResponseBase();
        responseBase.setRetBody(contentService.findArticleInfoAll(pageNum, pageSize, contentInfoRep));
        return new ResponseEntity(responseBase.success(), HttpStatus.OK);
    }

    @GetMapping("findContentById")
    @Transactional
    public ResponseEntity findContentById(ContentInfoRep contentInfoRep,HttpServletRequest request) {
        TokenModel accessToken = tokenManager.getToken(request.getHeader("accessToken"));
        if(!Optional.ofNullable(accessToken).isPresent()){
            contentService.browsing(contentInfoRep.getId());
        }
        ResponseBase responseBase = new ResponseBase();
        responseBase.setRetBody(contentService.findContentById(contentInfoRep.getId()));
        return new ResponseEntity(responseBase.success(), HttpStatus.OK);
    }

    @PostMapping("uploadCover")
    public ResponseEntity uploadCover(@RequestParam MultipartFile file, String userId) {
        String filePath = contentService.uploadCover(file, userId);
        ResponseBase responseBase = new ResponseBase();
        responseBase.setRetBody(filePath);
        return new ResponseEntity(responseBase.success(), HttpStatus.OK);
    }

    @GetMapping("showCover")
    public void showCover(@RequestParam String filePath, HttpServletResponse response) {
        FileUtils.showImage(envProperties.getCoverPath()+filePath, response);
    }

    private String getUserId(HttpServletRequest req) {
        TokenModel accessToken = tokenManager.getToken(req.getHeader("accessToken"));
        return accessToken.getUserId().split("&")[1];
    }
}

