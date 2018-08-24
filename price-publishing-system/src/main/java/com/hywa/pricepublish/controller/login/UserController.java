package com.hywa.pricepublish.controller.login;

import static com.hywa.pricepublish.common.enums.CommonEnum.RE_LOGIN;

import com.hywa.pricepublish.common.enums.CommonEnum;
import com.hywa.pricepublish.common.exception.GlobalException;
import com.hywa.pricepublish.config.EnvProperties;
import com.hywa.pricepublish.config.interceptor.TokenManager;
import com.hywa.pricepublish.config.interceptor.TokenModel;
import com.hywa.pricepublish.representation.NewUserInfoRep;
import com.hywa.pricepublish.representation.ResponseBase;
import com.hywa.pricepublish.representation.UserRep;
import com.hywa.pricepublish.representation.UserReps;
import com.hywa.pricepublish.representation.ext.UserRepExt;
import com.hywa.pricepublish.service.login.UserRoleService;
import com.hywa.pricepublish.service.login.UserService;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    private static final String MOBILE = "mobile";
    private static final String PC = "pc";
    @Autowired
    EnvProperties envProperties;

    @Autowired
    TokenManager tokenManager;

    @Autowired
    private UserService userService;

    @Autowired
    UserRoleService userRoleService;

    @GetMapping(value = "/login")
    public ResponseEntity<ResponseBase> login(
            @RequestParam String userName,
            @RequestParam String pwd,
            HttpServletRequest req) {
        ResponseBase<UserRep> userRepResponseBase = getUserRepResponseBase(
                userName, pwd, req, MOBILE);
        return new ResponseEntity<>(userRepResponseBase, HttpStatus.OK);
    }

    @GetMapping(value = "/pcLogin")
    public ResponseEntity<ResponseBase> pcLogin(
            @RequestParam String userName,
            @RequestParam String pwd,
            HttpServletRequest req) {
        ResponseBase<UserRep> userRepResponseBase = getUserRepResponseBase(userName, pwd, req,
                PC);
        return new ResponseEntity<>(userRepResponseBase, HttpStatus.OK);
    }

    private ResponseBase<UserRep> getUserRepResponseBase(String userName,
            String password, HttpServletRequest req, String equipment) {
        ResponseBase<UserRep> userRepResponseBase = new ResponseBase<>();
        UserRep userRep = userService.userLogin(userName, password);
        TokenModel token = tokenManager.createToken(equipment + "&" + userRep.getUserId());
        req.getSession().setAttribute("userId", userRep.getUserId());
        userRep.setAccessToken(equipment + "&" + userRep.getUserId() + "_" + token.getToken());
        userRepResponseBase.success();
        userRepResponseBase.setRetBody(userRep);
        return userRepResponseBase;
    }

    @GetMapping(value = "/checkToken")
    public ResponseEntity<ResponseBase> tokenVerify(
            @RequestParam String userId,
            @RequestParam String token) {
        ResponseBase<UserRep> userRepResponseBase = checkUserToken(userId, token, MOBILE);
        return new ResponseEntity<>(userRepResponseBase, HttpStatus.OK);
    }

    @GetMapping(value = "/pcCheckToken")
    public ResponseEntity<ResponseBase> pcTokenVerify(
            @RequestParam String userId,
            @RequestParam String token) {
        ResponseBase<UserRep> userRepResponseBase = checkUserToken(userId, token, PC);
        return new ResponseEntity<>(userRepResponseBase, HttpStatus.OK);
    }

    private ResponseBase<UserRep> checkUserToken(String userId, String token, String equipment) {
        String[] split = token.split("_");
        boolean result = tokenManager
                .checkToken(new TokenModel(equipment + "&" + userId, split[1]));
        ResponseBase<UserRep> userRepResponseBase = new ResponseBase<>();
        if (result) {
            UserRep userRep = userService.findById(userId);
            userRep.setAccessToken(token);
            userRepResponseBase.setRetBody(userRep);
            userRepResponseBase.success();
        } else {
            userRepResponseBase.setRetHead(RE_LOGIN.getIndex(), RE_LOGIN.getValue());
        }
        return userRepResponseBase;
    }


    @PostMapping
    public ResponseEntity<ResponseBase> registerUser(@Valid @RequestBody UserRepExt userRep,
            HttpServletRequest req) {
        TokenModel accessToken = tokenManager.getToken(req.getHeader("accessToken"));
        userRep.setCreateUser(accessToken.getUserId().split("&")[1]);
        userService.save(userRep);
        return new ResponseEntity<>(new ResponseBase().success(), HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public ResponseEntity<ResponseBase> logout(@RequestParam String userId) {
        tokenManager.deleteToken(MOBILE + "&" + userId);
        return new ResponseEntity<>(new ResponseBase().success(), HttpStatus.OK);

    }

    @RequestMapping(value = "/pcLogout", method = RequestMethod.DELETE)
    public ResponseEntity<ResponseBase> pcLogout(@RequestParam(value = "userId") String userId) {
        tokenManager.deleteToken(PC + "&" + userId);
        return new ResponseEntity<>(new ResponseBase().success(), HttpStatus.OK);

    }

    @DeleteMapping
    public ResponseEntity<ResponseBase> deleteUser(@RequestParam String userId,
            HttpServletRequest req) {
        TokenModel accessToken = tokenManager.getToken(req.getHeader("accessToken"));
        checkUpdateInfo(userId, accessToken.getUserId().split("&")[1]);
        userService.delete(userId);
        return new ResponseEntity<>(new ResponseBase().success(), HttpStatus.OK);

    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<ResponseBase> findAllUser(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String workUnit,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        ResponseBase<UserReps> responseBase = new ResponseBase<>();
        responseBase.success()
                .setRetBody(userService.findUsers(pageNum, pageSize, name, region, workUnit));
        return new ResponseEntity<>(responseBase, HttpStatus.OK);

    }

    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public ResponseEntity<ResponseBase<Map<String, Object>>> findAllById(String id)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ResponseBase<Map<String, Object>> responseBase = new ResponseBase<>();
        responseBase.success()
                .setRetBody(userService.findAllById(id));
        responseBase.success();
        return new ResponseEntity<>(responseBase, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<ResponseBase> update(
            @RequestParam String userId,
            @RequestBody UserRepExt userRep, HttpServletRequest req) {
        TokenModel accessToken = tokenManager.getToken(req.getHeader("accessToken"));
        checkUpdateInfo(userRep.getUserId(), accessToken.getUserId().split("&")[1]);
        ResponseBase responseBase = new ResponseBase<>();
        userService.update(userRep, userId);
        responseBase.success();
        return new ResponseEntity<>(responseBase, HttpStatus.OK);
    }

    @PutMapping("changePwd")
    public ResponseEntity changePwd(@RequestBody @Valid NewUserInfoRep newUserInfoRep) {
    	if(newUserInfoRep.getNewpassword().equals(newUserInfoRep.getPassword())) {
    		throw new GlobalException(CommonEnum.PASSWORD_REPEAT.getIndex(), CommonEnum.PASSWORD_REPEAT.getValue());
    	}
    	userService.changePwd(newUserInfoRep);
    	return new ResponseEntity<>(new ResponseBase().success(), HttpStatus.OK);
    }
    
    private void checkUpdateInfo(@RequestParam String userId, String currentUserId) {
        if (currentUserId.equals(userId)) {
            throw new GlobalException(CommonEnum.DEL_CURRENT_LOGIN_INFO.getIndex(),
                    CommonEnum.DEL_CURRENT_LOGIN_INFO.getValue());
        }
        if (envProperties.getAdminId().equals(userId)) {
            throw new GlobalException(CommonEnum.DEL_ADMIN.getIndex(),
                    CommonEnum.DEL_ADMIN.getValue());
        }
    }
}
