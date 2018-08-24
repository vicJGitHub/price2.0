package com.hywa.pricepublish.service.login.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.hywa.pricepublish.common.ConstantPool;
import com.hywa.pricepublish.common.enums.CommonEnum;
import com.hywa.pricepublish.common.exception.GlobalException;
import com.hywa.pricepublish.common.utils.StringUtils;
import com.hywa.pricepublish.common.utils.UUIDUtils;
import com.hywa.pricepublish.config.EnvProperties;
import com.hywa.pricepublish.dao.entity.Menu;
import com.hywa.pricepublish.dao.entity.MenuExample;
import com.hywa.pricepublish.dao.entity.MenuExample.Criteria;
import com.hywa.pricepublish.dao.entity.RoleMenuExample;
import com.hywa.pricepublish.dao.entity.RoleMenuKey;
import com.hywa.pricepublish.dao.mapper.MenuMapper;
import com.hywa.pricepublish.dao.mapper.RoleMenuMapper;
import com.hywa.pricepublish.representation.MenuRep;
import com.hywa.pricepublish.representation.MenuReps;
import com.hywa.pricepublish.service.login.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuMapper menuMapper;

	@Autowired
	private RoleMenuMapper roleMenuMapper;

	private static final String PARENT_ID = "#";
	
	@Autowired
    EnvProperties envProperties;

	@Override
	public int save(MenuRep menuRep) {
		if (!StringUtils.isEmpty(menuRep.getName()) && !nameIsEmpty(menuRep.getName(), null)) {
			throw new GlobalException(CommonEnum.MENU_NAME_REPEATED.getIndex(),
					CommonEnum.MENU_NAME_REPEATED.getValue());
		}
		Menu menu = new Menu();
		String menuId=UUIDUtils.randomUUID();
		menu.setId(menuId);
		menu.setCreateTime(new Date());
		menu.setCreateUser(menuRep.getCreateUser());
		menu.setName(menuRep.getName());
		menu.setSort((short) (findSortByParentId(menuRep.getParentId()) + 1));
		menu.setStatus(menuRep.getStatus());
		menu.setParentId(menuRep.getParentId());
		menu.setDescription(menuRep.getDescription());
		menu.setIcon(menuRep.getIcon());
		menu.setType(menuRep.getType());
		RoleMenuKey roleMenuKey=new RoleMenuKey(envProperties.getAdminRoleId(),menuId);
		roleMenuMapper.insert(roleMenuKey);
		return menuMapper.insertSelective(menu);
	}

	@Override
	public int update(MenuRep menuRep) {
		if (!StringUtils.isEmpty(menuRep.getName()) && !nameIsEmpty(menuRep.getName(), menuRep.getId())) {
			throw new GlobalException(CommonEnum.MENU_NAME_REPEATED.getIndex(),
					CommonEnum.MENU_NAME_REPEATED.getValue());
		}
		Menu menu = new Menu();
		BeanUtils.copyProperties(menuRep, menu, "createTime", "createUser");
		menu.setUpdateTime(new Date());
		return menuMapper.updateByPrimaryKeySelective(menu);
	}

	@Override
	public int delete(String id) {
		RoleMenuExample roleMenuExample = new RoleMenuExample();
		roleMenuExample.createCriteria().andMenuIdEqualTo(id).andRoleIdNotEqualTo(envProperties.getAdminRoleId());
		if (!CollectionUtils.isEmpty(roleMenuMapper.selectByExample(roleMenuExample))) {
			throw new GlobalException(CommonEnum.TYPE_REFERENCED.getIndex(), CommonEnum.TYPE_REFERENCED.getValue());
		}
		return menuMapper.deleteByPrimaryKey(id);
	}

	@Override
	public List<MenuRep> getChildren(String userId, String parentMenuId) {
		List<Menu> menuList = menuMapper.selectByUserIdAndParentId(userId, parentMenuId);
		List<MenuRep> menuReps = new ArrayList<>();
		menuList.forEach(menu -> menuReps.add(new MenuRep(menu)));
		return menuReps;
	}

	@Override
	public List<MenuRep> getMenuTreeByUserId(String userId, String requestMode) {
		List<Menu> menuList = menuMapper.selectMenusByUserId(userId);
		return getAllMenuRep(menuList, PARENT_ID, requestMode);
	}

	@Override
	public List<MenuRep> selectMenusByRoleId(String roleId) {
		List<Menu> menuList = menuMapper.selectByRoleId(roleId);
		return getAllMenuRep(menuList, PARENT_ID, "0");
	}

	@Override
	public List<MenuRep> getMenuAllTree(String roleId) {
		List<Menu> menus = menuMapper.selectAllOrRoleId(roleId);
		return getAllMenuRep(menus, PARENT_ID, ConstantPool.REQUEST_MODE_ALL);
	}

	private List<MenuRep> getAllMenuRep(List<Menu> menuList, String parentId, String requestMode) {
		List<MenuRep> menuReps = new ArrayList<>();
		for (Menu menu : menuList) {
			if (menu == null) {
				continue;
			}

			if (menu.getParentId().equals(parentId)) {
				MenuRep menuRep = new MenuRep(menu);
				List<MenuRep> allMenuRep;
				if (ConstantPool.REQUEST_MODE_ALL.equals(requestMode)) {
					allMenuRep = getAllMenuRep(menuList, menu.getId(), requestMode);
				} else {
					allMenuRep = findChildrenMenus(menuList, menu.getId());
				}
				menuRep.setChildMenus(allMenuRep);
				menuReps.add(menuRep);
			}
		}
		return menuReps;
	}

	private List<MenuRep> findChildrenMenus(List<Menu> menuList, String parentId) {
		List<MenuRep> menuRepList = new ArrayList<>();
		for (Menu menu : menuList) {
			if (menu == null) {
				continue;
			}
			if (menu.getParentId().equals(parentId)) {
				MenuRep menuRep1 = new MenuRep(menu);
				menuRepList.add(menuRep1);
			}
		}
		return menuRepList;
	}

	@Override
	public MenuReps search(int pageNum, int pageSize, String parentId, String id) {
		MenuExample example = new MenuExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDelEqualTo(ConstantPool.NOT_DEL);
		criteria.andStatusEqualTo((short) 0);
		criteria.andTypeEqualTo((short) 0);
		if (StringUtils.isNotEmpty(id)) {
			criteria.andIdEqualTo(id);
		}
		if (StringUtils.isNotEmpty(parentId)) {
			criteria.andIdNotEqualTo(parentId);
			criteria.andParentIdNotEqualTo(parentId);
		}
		example.setOrderByClause("sort DESC");
		List<Menu> menus = menuMapper.selectByExample(example);
		List<MenuRep> list = new ArrayList<>();
		menus.forEach(menu -> list.add(new MenuRep(menu)));
		return new MenuReps(list, list.size());
	}

	public short findSortByParentId(String parentId) {
		MenuExample example = new MenuExample();
		example.createCriteria().andParentIdEqualTo(parentId);
		List<Menu> menus = menuMapper.selectByExample(example);
		menus.sort((o1, o2) -> o2.getSort() - o1.getSort());
		return CollectionUtils.isEmpty(menus) ? 0 : (null == menus.get(0).getSort() ? 0 : menus.get(0).getSort());
	}

	@Override
	public boolean nameIsEmpty(String username, String id) {
		MenuExample menuExample = new MenuExample();
		MenuExample.Criteria criteria = menuExample.createCriteria();
		criteria.andNameEqualTo(username);
		criteria.andIsDelEqualTo(ConstantPool.NOT_DEL);
		if (StringUtils.isNotEmpty(id)) {
			criteria.andIdNotEqualTo(id);
		}
		return menuMapper.countByExample(menuExample) > 0 ? false : true;
	}

	@Override
	public List<MenuRep> findMenuAllTree() {
		MenuExample example = new MenuExample();
		example.createCriteria().
		andIsDelEqualTo(ConstantPool.NOT_DEL).
		andStatusEqualTo((short) 0).
		andTypeEqualTo((short) 0);
		List<Menu> menus = menuMapper.selectByExample(example);
		return getAllMenuRep(menus, PARENT_ID, ConstantPool.REQUEST_MODE_ALL);
	}
}
