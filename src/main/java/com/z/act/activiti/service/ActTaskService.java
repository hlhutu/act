package com.z.act.activiti.service;

import com.z.act.activiti.comm.TaskAction;
import com.z.act.help.RestResult;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ActTaskService {
    @Autowired
    IdentityService identityService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    TaskService taskService;
    @Autowired
    ProcessJumpService processJumpService;

    /**
     * 做任务
     * @param action
     * @param id
     * @param targetTaskKey
     * @return
     */
    public RestResult doTask(String action, String id, String targetTaskKey) throws Exception {
        if(TaskAction.start.equals(action)){
            return this.start(id);
        }else if(TaskAction.complete.equals(action)){
            return this.complete(id);
        }else if(TaskAction.back.equals(action)){
            return this.back(id);
        }else if(TaskAction.reject.equals(action)){
            return this.reject(id);
        }else if(TaskAction.jump.equals(action)){
            return this.jump(id, targetTaskKey);
        }else if(TaskAction.assign.equals(action)){
            return this.assign(id);
        }else if(TaskAction.cancel.equals(action)){
            return this.cancel(id);
        }else if(TaskAction.delete.equals(action)){
            return this.delete(id);
        }else {
            return new RestResult().fail("未知的处理方式：" + action);
        }
    }

    private RestResult start(String procdefId) {
        return new RestResult().fail("接口未实现");
    }

    private RestResult complete(String taskId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        identityService.setAuthenticatedUserId(userDetails.getUsername());//认证用户
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        //1.获取该任务
        taskService.setOwner(task.getId(), userDetails.getUsername());
        // 完成任务
        taskService.complete(task.getId());

        return new RestResult();
    }

    private RestResult back(String taskId) throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        identityService.setAuthenticatedUserId(userDetails.getUsername());//认证用户
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        processJumpService.jump(task, processJumpService.DIRECTION_BACKWARD,1);//后退一步
        return new RestResult();
    }

    private RestResult reject(String taskId) throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        identityService.setAuthenticatedUserId(userDetails.getUsername());//认证用户
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        processJumpService.jump(task, processJumpService.DIRECTION_BACKWARD, -1);//后退到开始
        return new RestResult();
    }

    private RestResult jump(String taskId, String targetTaskKey) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        identityService.setAuthenticatedUserId(userDetails.getUsername());//认证用户
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        processJumpService.jump(task, targetTaskKey);//跳转到目标节点
        return new RestResult();
    }

    private RestResult assign(String taskId) {
        return new RestResult().fail("接口未实现");
    }

    private RestResult cancel(String taskId) {
        return new RestResult().fail("接口未实现");
    }

    private RestResult delete(String taskId) {
        return new RestResult().fail("接口未实现");
    }
}
