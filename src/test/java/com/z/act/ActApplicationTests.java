package com.z.act;

import org.activiti.engine.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActApplicationTests {

    Logger logger = LogManager.getRootLogger();

    @Test
    public void contextLoads(){
    }

    @Autowired
    private ProcessEngine processEngine;//流程引擎，内置以下所有的工具

    @Autowired
    private RepositoryService repositoryService;//流程部署，定义等静态模块

    @Autowired
    private RuntimeService runtimeService;//运行时的流程实例管理等动态

    @Autowired
    private TaskService taskService;//任务管理

    @Autowired
    private HistoryService historyService;//历史数据管理

    @Autowired
    private IdentityService identityService;//人员，组等管理

    @Autowired
    private FormService formService;//表单管理

    @Autowired
    private ManagementService managementService;//整体管理服务

    @Test
    public void deploy() throws IOException {//部署一个流程图
    }
}
