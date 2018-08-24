package com.hywa.pricepublish.controller.login;

import static com.hywa.pricepublish.common.enums.CommonEnum.FAILURE;

import com.hywa.pricepublish.common.exception.GlobalException;
import com.hywa.pricepublish.config.interceptor.TokenManager;
import com.hywa.pricepublish.config.interceptor.TokenModel;
import com.hywa.pricepublish.dao.entity.Menu;
import com.hywa.pricepublish.representation.MenuRep;
import com.hywa.pricepublish.representation.MenuReps;
import com.hywa.pricepublish.representation.ResponseBase;
import com.hywa.pricepublish.service.login.MenuService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("menu")
public class MenuController {
    //TODO 魔法数

    @Autowired
    private MenuService menuService;

    @Autowired
    TokenManager tokenManager;

    @RequestMapping(value = "/getChildren", method = RequestMethod.GET)
    public ResponseEntity<ResponseBase> getChildren(
            @RequestParam String menuId,
            @RequestParam String userId) {
        List<MenuRep> menuRepList = menuService.getChildren(userId, menuId);
        ResponseBase<MenuReps> menuRepResponseBase = new ResponseBase<>();
        menuRepResponseBase.setRetBody(new MenuReps(menuRepList));
        menuRepResponseBase.success();
        return new ResponseEntity<>(menuRepResponseBase, HttpStatus.OK);
    }

    @RequestMapping(value = "/userMenuTree", method = RequestMethod.GET)
    public ResponseEntity<ResponseBase> getMenuTreeById(
            @RequestParam String userId,
            @RequestParam String requestMode) {
        List<MenuRep> menuRepList = menuService.getMenuTreeByUserId(userId, requestMode);

        ResponseBase<MenuReps> menuRepResponseBase = new ResponseBase<>();
        MenuReps menuReps = new MenuReps(menuRepList);
        menuRepResponseBase.setRetBody(menuReps);
        menuRepResponseBase.success();
        return new ResponseEntity<>(menuRepResponseBase, HttpStatus.OK);
    }

    @RequestMapping(value = "/menuAllTree", method = RequestMethod.GET)
    public ResponseEntity<ResponseBase> getMenuAllTree(
            @RequestParam(defaultValue = "") String roleId) {
        List<MenuRep> menuRepList = menuService.getMenuAllTree(roleId);

        ResponseBase<MenuReps> menuRepResponseBase = new ResponseBase<>();
        MenuReps menuReps = new MenuReps(menuRepList);
        menuRepResponseBase.setRetBody(menuReps);
        menuRepResponseBase.success();
        return new ResponseEntity<>(menuRepResponseBase, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/getMenus", method = RequestMethod.GET)
    public ResponseEntity<ResponseBase> getMenusByRoleId(@RequestParam String roleId) {
        List<MenuRep> menuRepList = menuService.selectMenusByRoleId(roleId);

        ResponseBase<MenuReps> responseBase = new ResponseBase<>();
        MenuReps menuReps = new MenuReps(menuRepList);
        responseBase.success();
        responseBase.setRetBody(menuReps);
        return new ResponseEntity<>(responseBase, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseBase> save(@RequestBody MenuRep menuRep, HttpServletRequest req) {
        TokenModel accessToken = tokenManager.getToken(req.getHeader("accessToken"));
        menuRep.setCreateUser(accessToken.getUserId().split("&")[1]);
        if (1 != menuService.save(menuRep)) {
            throw new GlobalException(FAILURE.getIndex(), "保存失败");
        }
        ResponseBase<Menu> menuRepResponseBase = new ResponseBase<>();
        menuRepResponseBase.setRetBody(menuRep);
        menuRepResponseBase.success();
        return new ResponseEntity<>(menuRepResponseBase, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ResponseBase> update(@RequestBody MenuRep menuRep,
            HttpServletRequest req) {
        TokenModel accessToken = tokenManager.getToken(req.getHeader("accessToken"));
        menuRep.setUpdateUser(accessToken.getUserId().split("&")[1]);
        if (1 != menuService.update(menuRep)) {
            throw new GlobalException(FAILURE.getIndex(), "更新失败");
        }
        return new ResponseEntity<>(new ResponseBase<>().success(), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<ResponseBase> delete(@RequestParam String menuId) {
        menuService.delete(menuId);
        return new ResponseEntity<>(new ResponseBase<>().success(), HttpStatus.OK);
    }

    //查询菜单基本信息
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<ResponseBase> search(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String id
            ) {

        ResponseBase menuRepResponseBase = new ResponseBase<>();
        menuRepResponseBase.success();
        menuRepResponseBase.setRetBody(menuService.search(pageNum, pageSize, parentId,id));
        return new ResponseEntity<>(menuRepResponseBase, HttpStatus.OK);
    }
}