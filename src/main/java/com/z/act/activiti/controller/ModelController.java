package com.z.act.activiti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.z.act.help.RestResult;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 流程图管理
 */
@RestController
@RequestMapping("model")
public class ModelController {
    private static final Logger logger = LogManager.getLogger(ModelController.class);

	@Autowired
	private RepositoryService repositoryService;

    /**
     * 视图，流程图设计页面
     * 需要一个参数，modelId：设计的流程图id
     * @return
     */
    @RequestMapping("editor")
    public ModelAndView modeler(){
        ModelAndView mav = new ModelAndView("activiti/modeler");
        return mav;
    }

    /**
     * 视图，流程图管理页面
     * models：已创建的流程图列表
     * @return
     */
    @RequestMapping
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView("activiti/model");
        List<Model> models = repositoryService.createModelQuery().orderByLastUpdateTime().desc().list();
        mav.addObject("models", models);
        return mav;
    }

    /**
     * 创建流程图，会跳转到流程图设计页面
     * @param response
     * @param name 流程图名，在创建之前输入
     * @param key 流程图key，在创建之前输入
     * @throws IOException
     */
    @RequestMapping(value = "create/{name}/{key}")
    public void create(HttpServletResponse response, @PathVariable String name, @PathVariable String key) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        Model modelData = repositoryService.newModel();

        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, "");
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(name);
        modelData.setKey(StringUtils.defaultString(key));

        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes(StandardCharsets.UTF_8));

        response.sendRedirect("/model/editor?modelId=" + modelData.getId());
    }

    /**
     * 删除一个流程图
     * @param modelId
     * @return
     */
    @RequestMapping(value = "delete")
    public RestResult delete(String modelId){
        repositoryService.deleteModel(modelId);
        return new RestResult("删除流程图成功");
    }

    /**
     * 部署一个流程图
     * @param modelId
     * @return 部署后的流程定义
     */
    @RequestMapping(value = "deploy")
    public RestResult deploy(String modelId, String modelKey) throws IOException {
        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        Deployment deployment = repositoryService.createDeployment()
                .addBpmnModel(modelKey + ".bpmn20.xml", bpmnModel)
                .deploy();
        return new RestResult("部署流程图成功").setData(deployment.getId());
    }
}