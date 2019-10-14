package com.z.act.activiti.plugins.listener;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class MyExeListener implements ExecutionListener {
    private static final Logger logger = LogManager.getRootLogger();

    @Override
    public void notify(DelegateExecution execution) {
        FlowElement flowElement = execution.getCurrentFlowElement();
        logger.info("{}-{}: {}({})", execution.getParentId(), execution.getId(), flowElement.getName());
    }
}
