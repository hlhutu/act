package com.z.act.core.security.service;

import com.z.act.core.security.bean.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface SysUserService extends UserDetailsService {

    int insertUser(SysUser sysUser) throws Exception;

    SysUser queryUser(SysUser sysUser) throws Exception;

    SysUser queryUserByUsername(String userName) throws Exception;
}
