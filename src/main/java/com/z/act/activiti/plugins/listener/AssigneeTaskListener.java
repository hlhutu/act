package com.z.act.activiti.plugins.listener;

import com.z.act.activiti.comm.AtcivitiHelper;
import com.z.act.activiti.comm.Const;
import com.z.act.activiti.comm.CustomStencilConstants;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 任务到达时，自动派单
 */
public class AssigneeTaskListener implements TaskListener {
    private static final Logger logger = LogManager.getRootLogger();

    @Override
    public void notify(DelegateTask delegateTask) {
        if(!isSingleAssign(delegateTask)){
            return;
        }
        logger.info("进行派单: {}", delegateTask.getName());
    }

    private boolean isSingleAssign(DelegateTask delegateTask){
        return Const.TRUE.equals(AtcivitiHelper.getDelegateTaskProperty(delegateTask.getExecution().getCurrentFlowElement(), CustomStencilConstants.SINGLE_ASSIGN));
    }
}
