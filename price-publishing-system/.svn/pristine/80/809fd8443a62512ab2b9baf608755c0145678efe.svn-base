package com.hywa.pricepublish.service.login;

import com.hywa.pricepublish.representation.MenuRep;
import com.hywa.pricepublish.representation.MenuReps;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

public interface MenuService {

    int save(MenuRep menuRep);

    int delete(String id);

    int update(MenuRep menuRep);

    List<MenuRep> getChildren(String userId, String menuId);

    List<MenuRep> getMenuTreeByUserId(String menuId, String requestMode);

    List<MenuRep> selectMenusByRoleId(String roleId);

    List<MenuRep> getMenuAllTree(String roleId);
    
    List<MenuRep> findMenuAllTree();
    
    MenuReps search( int pageNum, int pageSize,String name,String id);
    
    short findSortByParentId(String parentId);
    
    boolean nameIsEmpty(String username,String id);
}
