package com.z.act.core.security.mapper;

import com.z.act.core.security.bean.SysRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysRoleMapper {
    int insertRole(SysRole sysRole) throws Exception;

    SysRole queryRole(SysRole sysRole) throws Exception;
}
