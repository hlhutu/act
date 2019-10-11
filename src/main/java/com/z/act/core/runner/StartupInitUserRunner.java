package com.z.act.core.runner;

import com.z.act.core.security.bean.SysRole;
import com.z.act.core.security.bean.SysUser;
import com.z.act.core.security.mapper.SysMapper;
import com.z.act.core.security.service.SysRoleService;
import com.z.act.core.security.service.SysUserService;
import lombok.Data;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.sql.Connection;
import java.util.List;

/**
 * 启动后初始化用户
 */
@Component
@ConfigurationProperties("sys-default")
@Data
public class StartupInitUserRunner implements CommandLineRunner {

    private final Logger logger = LogManager.getLogger(StartupInitUserRunner.class);

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysMapper sysMapper;
    @Autowired
    private SqlSession sqlSession;

    private List<SysRole> adminRole;

    private List<SysUser> adminUser;

    private List<SysRole> testRole;

    private List<SysUser> testUser;

    List<ProcessDispatcherInfo> chain;

    @Order(999)//数值越大，越后执行
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) throws Exception {
        //初始化表结构
        initDatabaseSchema();
        //初始化测试用户
        initDefaultUser(adminRole, adminUser);
        initDefaultUser(testRole, testUser);
    }

    @Transactional(rollbackFor = Exception.class)
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

    private final String SCHEMA_SYS_USER = "sys_user";
    private final String SCHEMA_SYS_USER_SQL = "static/sql/sys_user.sql";
    private final String SCHEMA_SYS_ROLE = "sys_role";
    private final String SCHEMA_SYS_ROLE_SQL = "static/sql/sys_role.sql";
    private final String SCHEMA_SYS_USER_ROLE = "sys_user_role";
    private final String SCHEMA_SYS_USER_ROLE_SQL = "static/sql/sys_user_role.sql";

    private void initDatabaseSchema() throws Exception {
        initDatabaseSchema_sys_user();
        initDatabaseSchema_sys_role();
        initDatabaseSchema_sys_user_role();
    }

    private void initDatabaseSchema_sys_user() throws Exception {
        boolean exist =tableExist(SCHEMA_SYS_USER);
        if(exist){
            return;
        }
        execute(SCHEMA_SYS_USER_SQL);
    }

    private void initDatabaseSchema_sys_role() throws Exception {
        boolean exist =tableExist(SCHEMA_SYS_ROLE);
        if(exist){
            return;
        }
        execute(SCHEMA_SYS_ROLE_SQL);
    }

    private void initDatabaseSchema_sys_user_role() throws Exception {
        boolean exist =tableExist(SCHEMA_SYS_USER_ROLE);
        if(exist){
            return;
        }
        execute(SCHEMA_SYS_USER_ROLE_SQL);
    }

    private boolean tableExist(String tableName) {
        List tables = sysMapper.showTables(tableName);
        if(tables==null || tables.size()<=0){
            return false;
        }else{
            return true;
        }
    }

    private void execute(String filePath) throws Exception {
        Connection connection = null;
        try {

            InputStream in = this.getClass().getClassLoader().getResourceAsStream(filePath);
            Reader reader = new InputStreamReader(in);

            connection = sqlSession.getConnection();
            ScriptRunner runner = new ScriptRunner(connection);
            runner.setStopOnError(true);
            runner.runScript(reader);
            logger.info("已执行sql脚本：{}", filePath);
        }finally {
            if(connection!=null){
                //connection.close();
            }
        }
    }
}
