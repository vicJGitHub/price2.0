package com.hywa.pricepublish.service.content.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hywa.pricepublish.common.ConstantPool;
import com.hywa.pricepublish.common.enums.CommonEnum;
import com.hywa.pricepublish.common.exception.GlobalException;
import com.hywa.pricepublish.common.utils.FileUtils;
import com.hywa.pricepublish.common.utils.UUIDUtils;
import com.hywa.pricepublish.config.EnvProperties;
import com.hywa.pricepublish.dao.entity.Content;
import com.hywa.pricepublish.dao.entity.ContentInfo;
import com.hywa.pricepublish.dao.entity.ContentInfoExample;
import com.hywa.pricepublish.dao.mapper.ContentInfoMapper;
import com.hywa.pricepublish.dao.mapper.ContentMapper;
import com.hywa.pricepublish.representation.ContentInfoRep;
import com.hywa.pricepublish.representation.ContentInfoReps;
import com.hywa.pricepublish.service.collect.FileService;
import com.hywa.pricepublish.service.content.ContentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    ContentInfoMapper contentInfoMapper;

    @Autowired
    FileService fileService;

    @Autowired
    EnvProperties envProperties;


    @Override
    @Transactional
    public void save(ContentInfoRep contentInfoRep) {
        String contentId = UUIDUtils.randomUUID();
        String contentInfoId = UUIDUtils.randomUUID();
        Content content = new Content();
        content.setId(contentId);
        content.setContent(contentInfoRep.getContent());
        content.setIsDel(contentInfoRep.getIsDel());
        int contentSaveCount =contentMapper.insert(content);
        ContentInfo contentInfo = new ContentInfo();
        BeanUtils.copyProperties(contentInfoRep, contentInfo, "id");
        contentInfo.setId(contentInfoId);
        contentInfo.setContentId(contentId);
        int contentInfoSaveCount = contentInfoMapper.insert(contentInfo);
        if(contentInfoSaveCount+contentSaveCount<2){
            throw new GlobalException(CommonEnum.FAILURE.getIndex(),CommonEnum.FAILURE.getValue());
        }
    }

    @Override
    @Transactional
    public void update(ContentInfoRep contentInfoRep) {
        Content content = new Content();
        content.setId(contentInfoRep.getContentId());
        content.setContent(contentInfoRep.getContent());
        content.setIsDel(contentInfoRep.getIsDel());
        int contentSaveCount =contentMapper.updateByPrimaryKeySelective(content);
        ContentInfo contentInfo = new ContentInfo();
        BeanUtils.copyProperties(contentInfoRep, contentInfo);
        int contentInfoSaveCount = contentInfoMapper.updateByPrimaryKeySelective(contentInfo);
        if(contentInfoSaveCount+contentSaveCount<2){
            throw new GlobalException(CommonEnum.FAILURE.getIndex(),CommonEnum.FAILURE.getValue());
        }
    }

    @Override
    public ContentInfoReps findArticleInfoAll(int pageNum, int pageSize, ContentInfoRep contentInfoRep) {
        PageHelper.startPage(pageNum,pageSize,true);
        ContentInfoExample contentInfoExample = new ContentInfoExample();
        //TODO 此处需预留条件查询
        ContentInfoExample.Criteria criteria = contentInfoExample.createCriteria();
        criteria.andIsDelEqualTo(ConstantPool.NOT_DEL);
        if(Optional.ofNullable(contentInfoRep.getIsRecommend()).isPresent()){
            criteria.andIsRecommendEqualTo(contentInfoRep.getIsRecommend());
        }
        if(Optional.ofNullable(contentInfoRep.getIsUserble()).isPresent()){
            criteria.andIsUserbleEqualTo(contentInfoRep.getIsUserble());
        }
        contentInfoExample.setOrderByClause("published_time,is_recommend desc");
        List<ContentInfo> contentInfos = contentInfoMapper.selectByExample(contentInfoExample);
        return new ContentInfoReps(PageInfo.of(contentInfos).getTotal(),contentInfos);
    }

    @Override
    public ContentInfoRep findContentById(String id) {
        return contentInfoMapper.findContentById(id);
    }

    @Override
    public String uploadCover(MultipartFile file,String userId) {
        String filePath = FileUtils.uploadAndCreateNewName(file, envProperties.getCoverPath() );
        return filePath;
    }

    @Override
    public void browsing(String id) {
        contentInfoMapper.browsing(id);
    }

}
