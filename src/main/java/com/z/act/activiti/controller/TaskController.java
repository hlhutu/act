package com.z.act.activiti.controller;

import com.z.act.activiti.service.ActTaskService;
import com.z.act.help.RestResult;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class TaskController {
    private static final Logger logger = LogManager.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;//任务管理
    @Autowired
    private HistoryService historyService;//历史数据管理
    @Autowired
    private ActTaskService actTaskService;

    /**
     * 视图，我的待办列表
     * @return
     */
    @RequestMapping("/user/{userName}/task")
    public List<Task> getTaskByUserName(@PathVariable String userName){
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(userName).orderByTaskCreateTime().desc().list();
        return tasks;
    }

    /**
     * 完成任务
     * @param action
     * @param id
     * @return
     */
    @RequestMapping("{action}/task/{id}")
    public RestResult doTask(
            @PathVariable String action, @PathVariable String id
            , String targetTaskKey
    ){
        try {
            RestResult restResult = actTaskService.doTask(action, id, targetTaskKey);
            return restResult;
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResult().fail(e.getMessage());
        }

    }
}
