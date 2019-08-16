package com.z.act.core.security.bean;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class SysUser implements Serializable {

    private String userName;

    private String userAlias;

    private String userPwd;

    private List<SysRole> roles;
}
