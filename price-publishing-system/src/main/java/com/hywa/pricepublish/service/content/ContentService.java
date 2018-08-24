package com.hywa.pricepublish.service.content;


import com.hywa.pricepublish.representation.ContentInfoRep;
import com.hywa.pricepublish.representation.ContentInfoReps;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ContentService {

    void save(ContentInfoRep prContentInfoRep);

    void update(ContentInfoRep prContentInfoRep);

    ContentInfoReps findArticleInfoAll(int pageNum, int pageSize, ContentInfoRep prContentInfoRep);

    ContentInfoRep findContentById(String id);

    String uploadCover(MultipartFile file,String userId);

    void browsing(String id);

}
