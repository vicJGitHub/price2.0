package com.hywa.pricepublish.service.login;

import com.hywa.pricepublish.dao.entity.User;
import com.hywa.pricepublish.representation.NewUserInfoRep;
import com.hywa.pricepublish.representation.RoleReps;
import com.hywa.pricepublish.representation.UserRep;
import com.hywa.pricepublish.representation.UserReps;
import com.hywa.pricepublish.representation.ext.UserRepExt;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;


public interface UserService {

    User findByName(String username);

    void save(UserRepExt userRep);

    UserReps findUsers(int pageNum, int pageSize, String name, String region, String workUnit);

    UserRep userLogin(String userName, String psw);

    boolean delete(String userId);

    void updateUserRole(String userId, List<String> roleReps);

    UserRep findById(String userId);

    boolean phoneIsEmpty(String phone);

    Map<String, Object> findAllById(String id)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;

    void update(UserRepExt userRep, String userId);

	boolean nameIsEmpty(String username, String id);
	
	void changePwd(NewUserInfoRep newUserInfoRep);
}
