package com.hywa.pricepublish.service.collect.impl;

import com.hywa.pricepublish.common.ConstantPool;
import com.hywa.pricepublish.common.utils.UUIDUtils;
import com.hywa.pricepublish.config.EnvProperties;
import com.hywa.pricepublish.dao.entity.File;
import com.hywa.pricepublish.dao.entity.FileExample;
import com.hywa.pricepublish.dao.mapper.FileMapper;
import com.hywa.pricepublish.service.collect.FileService;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    EnvProperties envProperties;

    @Autowired
    FileMapper fileMapper;

    @Override
    public void save(String userId, long size, String name, String description) {
        File file = new File();
        file.setId(UUIDUtils.randomUUID());
        file.setCreateTime(new Date());
        file.setCreateUser(userId);
        if (description != null) {
            file.setDescription(description);
        }
        file.setName(name);
        file.setSize(size);
        file.setPath(envProperties.getPriceCollectFilePath());
        file.setIsDel(ConstantPool.NOT_DEL);
        fileMapper.insert(file);
    }

	@Override
	public List<File> find() {
		return fileMapper.find();
	}

	@Override
	public File findById(String id) {
		FileExample fileExample=new FileExample();
		fileExample.createCriteria()
		.andIsDelEqualTo(ConstantPool.NOT_DEL)
		.andIdEqualTo(id);
		List<File>list=fileMapper.selectByExample(fileExample);
		return CollectionUtils.isEmpty(list)?null:list.get(0);
	}
}
