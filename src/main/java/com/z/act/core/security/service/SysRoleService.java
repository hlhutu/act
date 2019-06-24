package com.z.act.core.security.service;

import com.z.act.core.security.bean.SysRole;

public interface SysRoleService {

    int insertRole(SysRole sysRole) throws Exception;

    SysRole queryRole(SysRole sysRole) throws Exception;
}
