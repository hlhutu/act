package com.z.act.core.security.service.impl;

import com.z.act.core.security.bean.SysRole;
import com.z.act.core.security.bean.SysUser;
import com.z.act.core.security.mapper.SysUserMapper;
import com.z.act.core.security.service.SysUserService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {
    private Logger logger = LogManager.getLogger();
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private IdentityService identityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser sysUser) throws Exception {
        org.activiti.engine.identity.User actUser = identityService.newUser(sysUser.getUserName());//新增人
        actUser.setFirstName(sysUser.getUserAlias());
        actUser.setPassword(actUser.getPassword());
        identityService.saveUser(actUser);
        Group actGroup;
        for(SysRole role : sysUser.getRoles()){
            actGroup = identityService.createGroupQuery().groupName(role.getRoleName()).singleResult();//查询出组
            identityService.createMembership(actUser.getId(), actGroup.getId());//将人加入组
        }
        return sysUserMapper.insertUser(sysUser);
    }

    @Override
    public SysUser queryUser(SysUser sysUser) throws Exception { return sysUserMapper.queryUser(sysUser); }

    @Override
    public SysUser queryUserByUsername(String userName) throws Exception { return sysUserMapper.queryUserByUsername(userName); }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        SysUser u = new SysUser();
        u.setUserName(s);
        try {
            u = queryUser(u);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException(e.getMessage());
        }
        if(u==null){
            throw new UsernameNotFoundException("用户不存在");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (SysRole role : u.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        User user = new User(u.getUserName(), u.getUserPwd(), authorities);
        return user;
    }
}
