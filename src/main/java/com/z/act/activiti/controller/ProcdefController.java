package com.z.act.activiti.controller;

import com.z.act.help.RestResult;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
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

/**
 * 流程定义管理，流程定义由流程图部署后产生
 */
@RestController
@RequestMapping("procdef")
public class ProcdefController {
    private static final Logger logger = LogManager.getLogger(ProcdefController.class);

    @Autowired
    private ProcessEngine processEngine;//流程引擎，内置以下所有的工具
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;//运行时的流程实例管理等动态模块

    /**
     * 视图，流程定义管理页面
     * @return
     */
    @RequestMapping
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView("activiti/procdef");
        List<ProcessDefinition> procdefs = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc().list();
        mav.addObject("procdefs", procdefs);
        return mav;
    }

    /**
     * 删除流程定义
     * @param deployId
     * @return
     */
    @RequestMapping("delete")
    public RestResult delete(String deployId){
        repositoryService.deleteDeployment(deployId, true);
        return new RestResult("删除流程定义成功");
    }

    /**
     * 启动流程
     * @param procdefKey
     * @return
     */
    @RequestMapping("start")
    public RestResult start(String procdefKey){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procdefKey);
        return new RestResult("启动流程成功").setData(processInstance.getId());
    }

    /**
     * 查看流程图
     * @param procdefId
     * @return
     */
    @RequestMapping("modelpng")
    public void model(String procdefId, HttpServletResponse response) throws IOException {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procdefId);
        ProcessDiagramGenerator processDiagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();

        InputStream diagram = processDiagramGenerator.generateDiagram(
                bpmnModel,"png", new ArrayList<>(), new ArrayList<>(),"黑体","黑体","黑体",null,0);

        response.setContentType("image/png");//以png形式输出
        byte[] b = new byte[1024];
        int len;
        while ((len = diagram.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
        response.getOutputStream().close();
    }
}
