package com.hywa.pricepublish.service.collect;

import java.util.List;

import com.hywa.pricepublish.dao.entity.File;

public interface FileService {

    void save(String userId, long size, String originalFileName,String description);
    
	List<File> find();
	
	File findById(String id);
}
