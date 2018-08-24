package com.hywa.pricepublish.representation.ext;

import com.hywa.pricepublish.representation.UserRep;
import java.io.Serializable;
import java.util.List;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString
public class UserRepExt extends UserRep implements Serializable {

    private static final long serialVersionUID = -7613694354941856913L;

    //角色信息
    private List<String> roles;

    //省id
    @NotNull(message = "区域不能为空")
    private String provinceId;

    private String provinceName;

    //市id
    private String cityId;

    private String cityName;

    //区id
    private String countyId;

    private String countyName;

    private String createUser;

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
}


