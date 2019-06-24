package com.z.act.core.security.service.impl;

import com.z.act.core.security.bean.SysRole;
import com.z.act.core.security.mapper.SysRoleMapper;
import com.z.act.core.security.service.SysRoleService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysRoleServiceImpl implements SysRoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private IdentityService identityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole sysRole) throws Exception {
        Group actGroup = identityService.newGroup(sysRole.getRoleName());
        actGroup.setName(sysRole.getRoleName());
        actGroup.setType("SYS_DAFAULT");
        identityService.saveGroup(actGroup);
        return sysRoleMapper.insertRole(sysRole);
    }

    @Override
    public SysRole queryRole(SysRole sysRole) throws Exception {
        return sysRoleMapper.queryRole(sysRole);
    }
}
