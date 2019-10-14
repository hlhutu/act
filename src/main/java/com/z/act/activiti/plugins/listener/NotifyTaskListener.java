package com.z.act.activiti.plugins.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 任务提醒
 */
public class NotifyTaskListener implements TaskListener {
    private static final Logger logger = LogManager.getRootLogger();

    @Override
    public void notify(DelegateTask delegateTask) {
        logger.info("发送通知: {}", delegateTask.getName());
    }
}
