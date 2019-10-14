package com.z.act.activiti.plugins;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class JumpCmd implements Command<Void> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Task currentTask;
    private Set<Task> deleteTasks;
    private Set<FlowElement> targetFlowElement;

    public JumpCmd(Task currentTask, Set<Task> deleteTasks, Set<FlowElement> targetFlowElement){
        this.currentTask = currentTask;
        this.deleteTasks = deleteTasks;
        this.targetFlowElement = targetFlowElement;
    }

    public Void execute(CommandContext commandContext){
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        // 删除
        for (Task task : deleteTasks) {
            taskEntityManager.deleteTask(taskEntityManager.findById(task.getId()),"系统删除", false, false);
        }
        // 跳转
        ExecutionEntity currentExe = executionEntityManager.findById(currentTask.getExecutionId());
        ExecutionEntity executionEntity;
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        for (FlowElement element : targetFlowElement) {
            executionEntity = executionEntityManager.createChildExecution(currentExe);
            executionEntity.setCurrentFlowElement(element);
            agenda.planContinueProcessInCompensation(executionEntity);
        }
        return null;
    }
}