package com.z.act.core.security.bean;

import java.io.Serializable;
import java.util.List;

public class SysUser implements Serializable {

    private String userName;

    private String userAlias;

    private String userPwd;

    private List<SysRole> roles;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String password) { this.userPwd = password; }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }
}
