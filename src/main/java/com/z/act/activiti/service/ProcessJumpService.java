package com.z.act.activiti.service;

import com.z.act.activiti.plugins.JumpCmd;
import com.z.act.help.RestResult;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 所有跳转，以流程图为标准，而不以任务执行顺序为标准
 */
@Service
public class ProcessJumpService {
    private static final Logger logger = LogManager.getRootLogger();
    @Autowired
    private HistoryService historyService;//历史数据管理
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ManagementService managementService;//整体管理服务

    public final String DIRECTION_FORWARD = "DIRECTION_FORWARD";
    public final String DIRECTION_BACKWARD = "DIRECTION_BACKWARD";

    /**
     * 指定跳转
     * @param task
     * @param taskDefKey
     * @return
     */
    public RestResult jump(Task task, String taskDefKey){
        JumpCmd cmd = this.makeJumpCmd(task, taskDefKey);
        //managementService.executeCommand(cmd);
        return new RestResult();
    }

    private JumpCmd makeJumpCmd(Task task, String taskDefKey) {
        Set<Task> deleteTasks = new HashSet<>();
        Set<FlowElement> targetFlowElement = new HashSet<>();

        /*BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        bpmnModel.getResources().forEach(res->{
            logger.info("== {}", res.getName());
            res.getAttributes().forEach((key, att)->{
                logger.info("==== {}", key);
                att.forEach(a->{
                    logger.info("====== {} - {}", a.getName(), a.getValue());
                });
            });
        });*/
        return null;
    }

    /**
     * 相对跳转，只能向前追溯
     * @param task
     * @param step 步长，0跳转到当前节点，-1跳转到结束
     * @return
     */
    public RestResult jump(Task task, String direction, int step) throws Exception {
        if(DIRECTION_FORWARD.equals(direction)){//向未来
            return new RestResult().fail("");
        }else {//向历史
            JumpCmd cmd = this.makeBackwardCmd(task, step);
            //managementService.executeCommand(cmd);
            return new RestResult();
        }
    }

    private JumpCmd makeBackwardCmd(Task task, int step) throws Exception {//向历史退
        logger.info("== 开始 ==");

        List<HistoricActivityInstance> activities = this.getHistActivities(task);

        /*Process process = this.findProcess(task);
        List<HistoricActivityInstance> hists = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .finished()
                .list();//已完成活动列表*/

        Set<FlowElement> targetFlowElement = new HashSet<>();//this.findTargetFlowElements(process, task.getTaskDefinitionKey(), hists, step);
        Set<Task> deleteTasks = new HashSet<>();

        logger.info("== 结束 ==");
        return new JumpCmd(task, deleteTasks, targetFlowElement);
    }

    private List<HistoricActivityInstance> getHistActivities(Task task) {
        return this.getHistActivities(task, true);
    }
    private List<HistoricActivityInstance> getHistActivities(Task task, boolean inside) {
        //当前分支的所有活动,开始时间正序排列
        List<HistoricActivityInstance> hists = historyService.createHistoricActivityInstanceQuery()
                .executionId(task.getExecutionId())
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        if(CollectionUtils.isEmpty(hists)){
            return new ArrayList<>();
        }
        //筛选其中开始时间，不晚于当前任务开始时间的活动
        hists = hists.stream().filter(hist->!hist.getStartTime().after(task.getCreateTime())).collect(Collectors.toList());

        Execution execution = runtimeService.createExecutionQuery()
                .executionId(task.getExecutionId())
                .singleResult();
        if(execution.getParentId()==null){
            hists.addAll(0, this.getHistActivities(null, false));
        }
        return hists;
    }

    private Set<FlowElement> findTargetFlowElements(Process process, String taskDefinitionKey, List<HistoricActivityInstance> hists, int step) {
        if(step<0){
            return this.findInitialFlowElements(process);
        }else {
            FlowElement currentFlowElement = process.getFlowElement(taskDefinitionKey);
            Set<FlowElement> set = Arrays.asList(currentFlowElement).stream().collect(Collectors.toSet());
            for(int i=0; i<step; i++){
                Set<FlowElement> newSet = new HashSet<>();
                set.forEach(node-> newSet.addAll(this.findPreFlowElements((FlowNode) node)));
                set = newSet;
            }
            return set;
        }
    }

    /**
     * 获取指定节点的下一步节点
     * @param currentNode
     * @return
     */
    private Set<FlowElement> findNextFlowElements(FlowNode currentNode) {
        Set<FlowElement> set = new HashSet<>();
        currentNode.getOutgoingFlows().forEach(line->{//遍历所有出口
            FlowElement targetFlowElement = line.getTargetFlowElement();
            if(targetFlowElement instanceof UserTask){//如果目标节点是用户任务，添加到结果集
                set.add(targetFlowElement);
            }else{//否则继续向后遍历一步
                set.addAll(findNextFlowElements((FlowNode) targetFlowElement));
            }
        });
        return set;
    }

    /**
     * 获取指定节点的前一步节点
     * @param currentNode
     * @return
     */
    private Set<FlowElement> findPreFlowElements(FlowNode currentNode) {
        Set<FlowElement> set = new HashSet<>();
        currentNode.getIncomingFlows().forEach(line->{//遍历所有入口
            FlowElement targetFlowElement = line.getTargetFlowElement();
            if(targetFlowElement instanceof UserTask){//如果目标节点是用户任务，添加到结果集
                set.add(targetFlowElement);
            }else{//否则继续向前遍历一步
                set.addAll(findNextFlowElements((FlowNode) targetFlowElement));
            }
        });
        return set;
    }

    /**
     * 获取流程的起始节点之后的所有节点
     * @param process
     * @return
     */
    private Set<FlowElement> findInitialFlowElements(Process process) {
        FlowNode startNode = (FlowNode) process.getInitialFlowElement();
        Set<FlowElement> set = this.findNextFlowElements(startNode);
        return set;
    }

    private Process findProcess(Task task) throws Exception {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();
        List<Process> processes = bpmnModel.getProcesses();
        processes = processes.stream().filter(proc->proc.getId().equals(processDefinition.getKey())).collect(Collectors.toList());
        if(processes.size()!=1){
            throw new Exception("未找到流程图");
        }
        Process process = processes.get(0);
        return process;
    }
}
