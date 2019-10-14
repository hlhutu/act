package com.z.act.activiti.plugins;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.z.act.activiti.comm.CustomStencilConstants;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.UserTaskJsonConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class CustomUserTaskJsonConverter extends UserTaskJsonConverter {

    private static final Logger logger = LogManager.getRootLogger();

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
        UserTask userTask = (UserTask) baseElement;
        //解析新增属性的业务逻辑
        userTask.getCustomProperties().forEach(prop->{
            logger.info("解析到属性：" + prop);
            this.setPropertyValue(prop.getName(), prop.getSimpleValue(), propertiesNode);
        });
    }
    
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap);
        UserTask userTask = (UserTask) flowElement;
        //singleAssign, 是否只派给一个人
        CustomProperty customProperty = new CustomProperty();
        customProperty.setName(CustomStencilConstants.SINGLE_ASSIGN);
        customProperty.setSimpleValue(this.getPropertyValueAsString(CustomStencilConstants.SINGLE_ASSIGN, elementNode));
        userTask.getCustomProperties().add(customProperty);
        return userTask;
    }

    public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomUserTaskJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, CustomUserTaskJsonConverter.class);
    }
}