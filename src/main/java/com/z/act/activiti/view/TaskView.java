package com.z.act.activiti.view;

import com.z.act.activiti.controller.TaskController;
import com.z.act.activiti.service.ActTaskService;
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
@RequestMapping
public class TaskView {
    private static final Logger logger = LogManager.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;//任务管理
    @Autowired
    private HistoryService historyService;//历史数据管理
    @Autowired
    private ActTaskService actTaskService;
    @Autowired
    private TaskController taskController;

    /**
     * 视图，我的待办列表
     * @return
     */
    @RequestMapping("task/mine")
    public ModelAndView mine(){
        ModelAndView mav = new ModelAndView("activiti/task");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Task> tasks = taskController.getTaskByUserName(userDetails.getUsername());
        mav.addObject("tasks", tasks);
        return mav;
    }

    /**
     * 视图，我的已办列表
     * @return
     */
    @RequestMapping("task/hist")
    public ModelAndView hist(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ModelAndView mav = new ModelAndView("activiti/histTask");
        List<HistoricTaskInstance> histTasks = historyService.createHistoricTaskInstanceQuery().taskAssignee(userDetails.getUsername())
                .orderByHistoricTaskInstanceEndTime().desc()
                .list();
        mav.addObject("histTasks", histTasks);
        return mav;
    }
}
