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
@RequestMapping("procinst")
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

    /**
     * 视图，流程实例管理页面
     * @return
     */
    @RequestMapping
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView("activiti/procinst");
        List<ProcessInstance> procinsts = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().desc().list();
        mav.addObject("procinsts", procinsts);
        return mav;
    }

    /**
     * 视图，历史流程实例管理页面
     * @return
     */
    @RequestMapping("hist")
    public ModelAndView hist(){
        ModelAndView mav = new ModelAndView("activiti/histProcinst");
        List<HistoricProcessInstance> histProcinsts = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceEndTime().desc()
                .list();
        mav.addObject("histProcinsts", histProcinsts);
        return mav;
    }

    /**
     * 删除流程实例
     * @param procinstId
     * @return
     */
    @RequestMapping("delete")
    public RestResult delete(String procinstId, String reason){
        runtimeService.deleteProcessInstance(procinstId, reason);
        return new RestResult("删除流程实例成功");
    }

    /**
     * 查看流程图
     * @param procinstId
     * @return
     */
    @RequestMapping("modelpng")
    public void model(String procinstId, String procdefId, HttpServletResponse response) throws IOException {
        //已进行的任务（包括当前正在进行的）
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procinstId).orderByHistoricActivityInstanceId().asc().list();
        List<String> executedActivityIdList = new ArrayList<>();
        activities.forEach(historicActivityInstance->executedActivityIdList.add(historicActivityInstance.getActivityId()));

        //已执行的线
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procdefId);
        List<String> executedFlowIdList = executedFlowIdList(bpmnModel, activities);
        ProcessDiagramGenerator processDiagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
        InputStream diagram = processDiagramGenerator.generateDiagram(
                bpmnModel,"png", executedActivityIdList, executedFlowIdList,"黑体","黑体","黑体",null, 0);

        response.setContentType("image/png");//以png形式输出
        byte[] b = new byte[1024];
        int len;
        while ((len = diagram.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
        response.getOutputStream().close();
    }

    private List<String> executedFlowIdList(BpmnModel bpmnModel, List<HistoricActivityInstance> activities) {
        List<String> executedFlowIdList = new ArrayList<>();

        for(int i=0;i<activities.size()-1;i++) {//遍历已执行的所有任务
            HistoricActivityInstance hai = activities.get(i);
            FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(hai.getActivityId());//获取该任务在图中的node
            List<SequenceFlow> sequenceFlows = flowNode.getOutgoingFlows();//获取该node的所有出口
            if(sequenceFlows.size()>1) {
                HistoricActivityInstance nextHai = activities.get(i+1);
                sequenceFlows.forEach(sequenceFlow->{
                    if(sequenceFlow.getTargetFlowElement().getId().equals(nextHai.getActivityId())) {
                        executedFlowIdList.add(sequenceFlow.getId());
                    }
                });
            }else {//如只有一个出口，则下一条线
                executedFlowIdList.add(sequenceFlows.get(0).getId());
            }
        }
        return executedFlowIdList;
    }
}
