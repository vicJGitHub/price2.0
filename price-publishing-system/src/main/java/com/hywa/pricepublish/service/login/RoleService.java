package com.hywa.pricepublish.service.login;

import com.hywa.pricepublish.representation.RoleRep;
import com.hywa.pricepublish.representation.RoleReps;

public interface RoleService {

    void save(RoleRep roleRep);

    int update(RoleRep roleRep);

    void delete(String id);

    RoleReps selectAll(int pageNum, int pageSize,String name);

    RoleReps selectRolesByUserId(int pageNum, int pageSize, String userId);
    
	boolean nameIsEmpty(String name, String id);
	
	RoleRep findById(String id);
}
