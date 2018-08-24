package com.hywa.pricepublish.service.login.impl;

import static com.hywa.pricepublish.common.enums.CommonEnum.ILLEGAL_PARAMETER_ERROR;
import static com.hywa.pricepublish.common.enums.CommonEnum.PASSWORD_ERROR;
import static com.hywa.pricepublish.common.enums.CommonEnum.TELEPHONE_FORMAL_ERROR;
import static com.hywa.pricepublish.common.enums.CommonEnum.UNBOUND_REGION_ERROR;
import static com.hywa.pricepublish.common.enums.CommonEnum.USER_CANCEL;
import static com.hywa.pricepublish.common.enums.CommonEnum.USER_NAME_REPEATED;
import static com.hywa.pricepublish.common.enums.CommonEnum.USER_UNREGISTERED_ERROR;
import static com.hywa.pricepublish.common.enums.CommonEnum.USERINFO_ERROR;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hywa.pricepublish.common.ConstantPool;
import com.hywa.pricepublish.common.enums.CommonEnum;
import com.hywa.pricepublish.common.exception.GlobalException;
import com.hywa.pricepublish.common.utils.StringUtils;
import com.hywa.pricepublish.dao.entity.User;
import com.hywa.pricepublish.dao.entity.UserArea;
import com.hywa.pricepublish.dao.entity.UserExample;
import com.hywa.pricepublish.dao.entity.UserExample.Criteria;
import com.hywa.pricepublish.dao.entity.UserExample.Criterion;
import com.hywa.pricepublish.dao.entity.UserRoleKey;
import com.hywa.pricepublish.dao.mapper.RoleMapper;
import com.hywa.pricepublish.dao.mapper.UserAreaMapper;
import com.hywa.pricepublish.dao.mapper.UserMapper;
import com.hywa.pricepublish.dao.mapper.UserRoleMapper;
import com.hywa.pricepublish.representation.NewUserInfoRep;
import com.hywa.pricepublish.representation.UserRep;
import com.hywa.pricepublish.representation.UserReps;
import com.hywa.pricepublish.representation.ext.UserRepExt;
import com.hywa.pricepublish.service.login.UserAreaService;
import com.hywa.pricepublish.service.login.UserRoleService;
import com.hywa.pricepublish.service.login.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	UserMapper userMapper;

	@Autowired
	RoleMapper roleMapper;

	@Autowired
	UserAreaMapper userAreaMapper;

	@Autowired
	RedisTemplate redisTemplate;

	@Autowired
	UserRoleMapper userRoleMapper;

	@Autowired
	private UserAreaService userAreaService;

	@Autowired
	UserRoleService userRoleService;

	@Override
	public void save(UserRepExt userRep) {
		User user = new User(userRep);
		if (!StringUtils.isEmpty(userRep.getName()) && !nameIsEmpty(userRep.getName(),null)) {
			throw new GlobalException(USER_NAME_REPEATED.getIndex(), USER_NAME_REPEATED.getValue());
		}
		if (!StringUtils.isEmpty(userRep.getPhone()) && !StringUtils.isCellPhone(userRep.getPhone())) {
			throw new GlobalException(TELEPHONE_FORMAL_ERROR.getIndex(), TELEPHONE_FORMAL_ERROR.getValue());
		}
		if (!CollectionUtils.isEmpty(userRep.getRoles())) {
			List<UserRoleKey> userRoleKeys = new ArrayList<>();
			userRep.getRoles().forEach(roles -> userRoleKeys.add(new UserRoleKey(user.getId(), roles)));
			userRoleService.insertBatch(userRoleKeys);
		}
		userMapper.insert(user);
		String regionId;
		short type;
		if (StringUtils.isNotEmpty(userRep.getCountyId())) {
			type = (short) 3;
			regionId = userRep.getCountyId();
		} else if (StringUtils.isNotEmpty(userRep.getCityId())) {
			type = (short) 2;
			regionId = userRep.getCityId();
		} else {
			type = (short) 1;
			regionId = userRep.getProvinceId();
		}
		userAreaMapper.insert(new UserArea(user.getId(), regionId, type));
	}

	@Override
	public UserRep findById(String userId) {
		User user = userMapper.selectByPrimaryKey(userId);
		String regionId = userAreaService.findRegionId(user.getId());

		return new UserRep(user.getId(), user.getName(), user.getTelephone(), user.getSex(), user.getJob(),
				user.getWorkUnit(), user.getAge(), regionId);
	}

	@Override
	public UserReps findUsers(int pageNum, int pageSize, String name, String region, String workUnit) {
		PageHelper.startPage(pageNum, pageSize, true);
		PageHelper.orderBy("create_time desc");
		List<User> users = userMapper.selectByRegionAndWorkUnit(name, region, workUnit);
		PageInfo<User> page = new PageInfo<>(users);
		List<UserRep> userRepList = new ArrayList<>();
		for (User user : users) {
			UserRep userRep = new UserRep(user);
			userRepList.add(userRep);
		}
		return new UserReps(page.getTotal(), userRepList);
	}

	@Override
	public User findByName(String userName) {
		return userMapper.selectByUserName(userName);
	}

	@Override
	public UserRep userLogin(String userName, String psw) {
		User user = userMapper.selectByUserName(userName);
		if (user == null) {
			// 用户未注册
			throw new GlobalException(USER_UNREGISTERED_ERROR.getIndex(), USER_UNREGISTERED_ERROR.getValue());
		} else if (user.getPassword().equals(StringUtils.md5(psw))) {
			if (user.getIsDel() == ConstantPool.DEL) {
				// 用户已经注销，请联系管理员
				throw new GlobalException(USER_CANCEL.getIndex(), USER_CANCEL.getValue());
			} else {
				String regionId = null;
				try {
					regionId = userAreaService.findRegionId(user.getId());
				} catch (Exception e) {
					// 此账户未绑定区域，请联系管理员！
					throw new GlobalException(UNBOUND_REGION_ERROR.getIndex(), UNBOUND_REGION_ERROR.getValue());
				}
				UserRep userRep = new UserRep(user.getId(), user.getName(), user.getTelephone(), user.getSex(),
						user.getJob(), user.getWorkUnit(), user.getAge(), regionId);
				return userRep;
			}
		} else {
			// 密码错误
			throw new GlobalException(PASSWORD_ERROR.getIndex(), PASSWORD_ERROR.getValue());
		}
	}

	@Override
	public boolean delete(String userId) {
		if (StringUtils.isEmpty(userId)) {
			throw new GlobalException(ILLEGAL_PARAMETER_ERROR.getIndex(), "userId不能为空");
		}
		if (!(userMapper.deleteByUserId(userId) > 0)) {
			return false;
		}
		userAreaMapper.deleteByUserId(userId);
		userRoleMapper.deleteByUserId(userId);
		return true;
	}

	@Override
	public boolean nameIsEmpty(String username,String id) {
		UserExample userExample = new UserExample();
		UserExample.Criteria criteria = userExample.createCriteria();
		criteria.andNameEqualTo(username);
		criteria.andIsDelEqualTo(ConstantPool.NOT_DEL);
		if(StringUtils.isNotEmpty(id)) {
			criteria.andIdNotEqualTo(id);
		}
		return userMapper.countByExample(userExample) > 0 ? false : true;
	}

	@Override
	public void updateUserRole(String userId, List<String> roleReps) {
		if (StringUtils.isEmpty(userId)) {
			throw new GlobalException(ILLEGAL_PARAMETER_ERROR.getIndex(), "userId不能为空");
		}
		userRoleMapper.deleteByUserId(userId);
		List<UserRoleKey> userRoleKeys = new ArrayList<>();
		for (String roleId : roleReps) {
			UserRoleKey userRoleKey = new UserRoleKey();
			userRoleKey.setRoleId(roleId);
			userRoleKey.setUserId(userId);
			userRoleKeys.add(userRoleKey);
		}
		userRoleMapper.insertBatch(userRoleKeys);
	}

	@Override
	public boolean phoneIsEmpty(String phone) {
		UserExample userExample = new UserExample();
		UserExample.Criteria criteria = userExample.createCriteria();
		criteria.andTelephoneEqualTo(phone);
		criteria.andIsDelEqualTo(ConstantPool.NOT_DEL);
		return userMapper.countByExample(userExample) > 0 ? false : true;
	}

	@Override
	public Map<String, Object> findAllById(String id) {
		Map<String, Object> map = new HashMap<>();
		map = userMapper.selectByIdUserInfo(id);

		if (CollectionUtils.isEmpty(map)) {
			return null;
		}
		if((StringUtils.isNotEmpty((String) map.get("region_id"))&&null!=map.get("type"))) {
		map.putAll(userMapper.selectByIdUserArea((String) map.get("region_id"), (int) map.get("type")));
		}
		return reMap(map);
	}

	// 转换格式
	private Map<String, Object> reMap(Map<String, Object> map) {
		Map<String, Object> reField = new HashMap<String, Object>();// 返回字段
		reField.put("id", map.get("id"));// 主键
		reField.put("name", map.get("name"));// 姓名
		reField.put("sex", 1 == (int) map.get("sex") ? "男" : "女");// 1男 2女
		reField.put("phone", map.get("telephone"));// 电话
		reField.put("account", map.get("account"));// MD5加密
		reField.put("email", map.get("email"));// 邮箱号码
		reField.put("updateUser", map.get("update_user"));// 更新人
		reField.put("createUser", map.get("create_user"));// 创建人
		reField.put("isDel", map.get("is_del"));// 0 未删 1 删除
		reField.put("jobTitle", map.get("job"));// 职务
		reField.put("type", map.get("type"));// 0 未删 1 删除
		reField.put("region", map.get("region_id"));//
		reField.put("workUnit", map.get("work_unit"));// 工作单位
		reField.put("age", map.get("age"));// 年龄
		reField.put("countyId", map.get("countyId"));// 区县id
		reField.put("cityId", map.get("cityId"));// 市id
		reField.put("provinceId", map.get("provinceId"));// 省id
		reField.put("countyName", map.get("countyName"));// 区县名
		reField.put("cityName", map.get("cityName"));// 市名
		reField.put("provinceName", map.get("provinceName"));// 省名
		reField.put("roles", map.get("roles"));// 角色id
		return reField;// 返回字段
	}

	@Override
	public void update(UserRepExt userRep, String userId) {
		if (!StringUtils.isEmpty(userRep.getName()) && !nameIsEmpty(userRep.getName(),userRep.getUserId())) {
			throw new GlobalException(USER_NAME_REPEATED.getIndex(), USER_NAME_REPEATED.getValue());
		}
		if (!StringUtils.isEmpty(userRep.getPhone()) && !StringUtils.isCellPhone(userRep.getPhone())) {
			throw new GlobalException(TELEPHONE_FORMAL_ERROR.getIndex(), TELEPHONE_FORMAL_ERROR.getValue());
		}
		User user = new User();
		user.setJob(userRep.getJobTitle());
		user.setSex(CommonEnum.getIndex(userRep.getSex()));
		user.setName(userRep.getName());
		user.setAge(userRep.getAge());
		user.setWorkUnit(userRep.getWorkUnit());
		user.setId(userRep.getUserId());
		user.setUpdateTime(new Date());
		user.setTelephone(userRep.getPhone());
		user.setUpdateUser(userId);
		
		userMapper.updateByPrimaryKey(user);
		updateUserArea(userRep);
		updateUserRole(userRep.getUserId(), userRep.getRoles());
	}

	private void updateUserArea(UserRepExt userRep) {
		short type;
		String regionId;

		UserArea record = new UserArea();
		record.setUserId(userRep.getUserId());

		if (StringUtils.isNotEmpty(userRep.getCountyId())) {
			type = (short) 3;
			regionId = userRep.getCountyId();
		} else if (StringUtils.isNotEmpty(userRep.getCityId())) {
			type = (short) 2;
			regionId = userRep.getCityId();
		} else {
			type = (short) 1;
			regionId = userRep.getProvinceId();
		}

		record.setType(type);
		record.setRegionId(regionId);
		userAreaMapper.updateByPrimaryKey(record);
	}

	@Override
	public void changePwd(NewUserInfoRep newUserInfoRep) {
		User user=userMapper.selectByPrimaryKey(newUserInfoRep.getId());
		if(null==user) {
			throw new GlobalException(USERINFO_ERROR.getIndex(), USERINFO_ERROR.getValue());
		}
		if(!user.getPassword().equals(StringUtils.md5(newUserInfoRep.getPassword()))) {
			throw new GlobalException(PASSWORD_ERROR.getIndex(), PASSWORD_ERROR.getValue());
		}
		User newUser=new User();
		newUser.setId(user.getId());
		newUser.setPassword(StringUtils.md5(newUserInfoRep.getNewpassword()));
		userMapper.updateByPrimaryKeySelective(newUser);
	}

}
