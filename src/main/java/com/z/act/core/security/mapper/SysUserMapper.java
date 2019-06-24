package com.z.act.core.security.mapper;

import com.z.act.core.security.bean.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {
    int insertUser(SysUser sysUser) throws Exception;

    SysUser queryUser(SysUser sysUser) throws Exception;

    SysUser queryUserByUsername(@Param("userName") String userName) throws Exception;
}
