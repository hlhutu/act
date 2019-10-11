package com.z.act.activiti.controller;

import com.z.act.help.RestResult;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class ProcinstController {
    private static final Logger logger = LogManager.getLogger(ProcinstController.class);

    @Autowired
    private ProcessEngine processEngine;//流程引擎，内置以下所有的工具
    @Autowired
    private RepositoryService repositoryService;//流程部署，定义等静态模块
    @Autowired
    private RuntimeService runtimeService;//运行时的流程实例管理等动态模块
    @Autowired
    private HistoryService historyService;//历史数据管理
}
