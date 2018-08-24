package com.hywa.pricepublish.service.login.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hywa.pricepublish.common.ConstantPool;
import com.hywa.pricepublish.common.enums.CommonEnum;
import com.hywa.pricepublish.common.exception.GlobalException;
import com.hywa.pricepublish.common.utils.StringUtils;
import com.hywa.pricepublish.common.utils.UUIDUtils;
import com.hywa.pricepublish.config.EnvProperties;
import com.hywa.pricepublish.dao.entity.Role;
import com.hywa.pricepublish.dao.entity.RoleExample;
import com.hywa.pricepublish.dao.entity.RoleMenuKey;
import com.hywa.pricepublish.dao.entity.UserExample;
import com.hywa.pricepublish.dao.mapper.RoleMapper;
import com.hywa.pricepublish.dao.mapper.RoleMenuMapper;
import com.hywa.pricepublish.dao.mapper.UserRoleMapper;
import com.hywa.pricepublish.representation.MenuRep;
import com.hywa.pricepublish.representation.RoleRep;
import com.hywa.pricepublish.representation.RoleReps;
import com.hywa.pricepublish.service.login.RoleService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    @Transactional
    public void save(RoleRep roleRep) {
    	if(!nameIsEmpty(roleRep.getName(),null)) {
    		throw new GlobalException(CommonEnum.ROLE_NAME_REPEATED.getIndex(), CommonEnum.ROLE_NAME_REPEATED.getValue());
    	}
        Role role = new Role();
        BeanUtils.copyProperties(roleRep, role, "code", "id");
        role.setCreateTime(new Date());
        role.setId(UUIDUtils.randomUUID());
        role.setIsDel(ConstantPool.NOT_DEL);
        roleMapper.insert(role);

        List<RoleMenuKey> roleMenus = new ArrayList<>();
        roleRep.getMenuReps().forEach(menu->{
        	roleMenus.add(new RoleMenuKey(role.getId(),String.valueOf(menu)));
        });
        roleMenuMapper.insertBatch(roleMenus);
    }

    @Override
    @Transactional
    public int update(RoleRep roleRep) {
    	if(!nameIsEmpty(roleRep.getName(),roleRep.getId())) {
    		throw new GlobalException(CommonEnum.ROLE_NAME_REPEATED.getIndex(), CommonEnum.ROLE_NAME_REPEATED.getValue());
    	}
        roleMenuMapper.deleteByRoleId(roleRep.getId());
        List<RoleMenuKey> roleMenus = new ArrayList<>();
        roleRep.getMenuReps().forEach(menu->{
        	roleMenus.add(new RoleMenuKey(roleRep.getId(),String.valueOf(menu)));
        });
        if(!CollectionUtils.isEmpty(roleMenus)) {
        	roleMenuMapper.insertBatch(roleMenus);
        }
        Role role = new Role();
        BeanUtils.copyProperties(roleRep, role, "code");
        role.setUpdateTime(new Date());
        return roleMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    @Transactional
    public void delete(String id) {
        roleMapper.deleteByPrimaryKey(id);
        roleMenuMapper.deleteByRoleId(id);
        userRoleMapper.deleteByRoleId(id);
    }

    @Override
    public RoleReps selectAll(int pageNum, int pageSize,String name) {
        PageHelper.startPage(pageNum, pageSize, true);
        List<Role> roles = roleMapper.selectAll(name);
        List<RoleRep> roleReps = new ArrayList<>();
        roles.forEach(role -> roleReps.add(new RoleRep(role)));
        return new RoleReps(roleReps, new PageInfo<>(roles).getTotal());
    }

    @Override
    public RoleReps selectRolesByUserId(int pageNum, int pageSize, String userId) {
        PageHelper.startPage(pageNum, pageSize, true);
        List<Role> roles = roleMapper.selectByUserId(userId);
        List<RoleRep> roleReps = new ArrayList<>();
        roles.forEach(role -> roleReps.add(new RoleRep(role)));
        return new RoleReps(roleReps, new PageInfo<>(roles).getTotal());
    }
    
    @Override
	public boolean nameIsEmpty(String name,String id) {
		RoleExample roleExample = new RoleExample();
		RoleExample.Criteria criteria = roleExample.createCriteria();
		criteria.andNameEqualTo(name);
		criteria.andIsDelEqualTo(ConstantPool.NOT_DEL);
		if(StringUtils.isNotEmpty(id)) {
			criteria.andIdNotEqualTo(id);
		}
		return roleMapper.countByExample(roleExample) > 0 ? false : true;
	/*	UserExample userExample = new UserExample();
		UserExample.Criteria criteria = userExample.createCriteria();
		criteria.andNameEqualTo(username);
		criteria.andIsDelEqualTo(ConstantPool.NOT_DEL);
		if(StringUtils.isNotEmpty(id)) {
			criteria.andIdNotEqualTo(id);
		}
		return userMapper.countByExample(userExample) > 0 ? false : true;*/
	}

	@Override
	public RoleRep findById(String id) {
		Role role=roleMapper.findById(id);
		return null==role?null:new RoleRep(role);
	}
    
    
}
