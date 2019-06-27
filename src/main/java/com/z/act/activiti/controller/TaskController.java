package com.z.act.activiti.controller;

import com.z.act.help.RestResult;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@RestController
@RequestMapping("task")
public class TaskController {
    private static final Logger logger = LogManager.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;//任务管理
    @Autowired
    private HistoryService historyService;//历史数据管理

    /**
     * 视图，我的待办列表
     * @return
     */
    @RequestMapping("mine")
    public ModelAndView index(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ModelAndView mav = new ModelAndView("activiti/task");
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(userDetails.getUsername()).orderByTaskCreateTime().desc().list();
        mav.addObject("tasks", tasks);
        return mav;
    }

    /**
     * 视图，我的已办列表
     * @return
     */
    @RequestMapping("hist")
    public ModelAndView hist(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ModelAndView mav = new ModelAndView("activiti/histTask");
        List<HistoricTaskInstance> histTasks = historyService.createHistoricTaskInstanceQuery().taskAssignee(userDetails.getUsername())
                .orderByHistoricTaskInstanceEndTime().desc()
                .list();
        mav.addObject("histTasks", histTasks);
        return mav;
    }

    /**
     * 完成任务
     * @param taskId
     * @return
     */
    @RequestMapping("complate")
    public RestResult complate(String taskId){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        task.setAssignee(userDetails.getUsername());//将处理人改为当前用户
        taskService.saveTask(task);
        taskService.complete(taskId);
        return new RestResult().success("任务已完成");
    }
}
