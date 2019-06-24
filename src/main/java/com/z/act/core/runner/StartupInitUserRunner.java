package com.z.act.core.runner;

import com.z.act.core.security.bean.SysRole;
import com.z.act.core.security.bean.SysUser;
import com.z.act.core.security.service.SysRoleService;
import com.z.act.core.security.service.SysUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 启动后初始化用户
 */
@Component
@ConfigurationProperties("sys-default")
public class StartupInitUserRunner implements CommandLineRunner {

    private final Logger logger = LogManager.getLogger(StartupInitUserRunner.class);

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;

    private List<SysRole> adminRole;

    private List<SysUser> adminUser;

    private List<SysRole> testRole;

    private List<SysUser> testUser;

    public List<SysRole> getTestRole() { return testRole; }

    public void setTestRole(List<SysRole> testRole) { this.testRole = testRole; }

    public List<SysUser> getTestUser() { return testUser; }

    public void setTestUser(List<SysUser> testUser) { this.testUser = testUser; }

    public List<SysRole> getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(List<SysRole> adminRole) {
        this.adminRole = adminRole;
    }

    public List<SysUser> getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(List<SysUser> adminUser) { this.adminUser = adminUser; }

    @Order(999)//数值越大，越后执行
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) throws Exception {
        initDefaultUser(adminRole, adminUser);
        initDefaultUser(testRole, testUser);
    }

    private void initDefaultUser(List<SysRole> roles, List<SysUser> users) throws Exception {
        for(SysRole role : roles){
            if(sysRoleService.queryRole(role)==null){
                sysRoleService.insertRole(role);
                logger.info("创建角色：" + role.getRoleName());
            }else{
                logger.debug("已有角色：" + role.getRoleName());
            }
        }
        for(SysUser user : users){
            if(sysUserService.queryUserByUsername(user.getUserName())==null){
                user.setRoles(roles);
                sysUserService.insertUser(user);
                logger.info("创建用户：" + user.getUserName() + " 密码：" + user.getUserPwd());
            }else{
                logger.debug("已有用户：" + user.getUserName() + " 密码：" + user.getUserPwd());
            }
        }
    }
}
