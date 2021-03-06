package com.z.act.activiti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.z.act.activiti.plugins.CustomBpmnJsonConverter;
import com.z.act.help.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 流程图管理
 */
@RestController
@RequestMapping
@Api(tags = {"流程图"})
public class ModelController {
    private static final Logger logger = LogManager.getLogger(ModelController.class);

	@Autowired
	private RepositoryService repositoryService;

    /**
     * 视图，流程图设计页面
     * 需要一个参数，modelId：设计的流程图id
     * @return
     */
    @RequestMapping("model/editor")
    public ModelAndView modeler(){
        ModelAndView mav = new ModelAndView("activiti/modeler");
        return mav;
    }

    /**
     * 视图，流程图管理页面
     * models：已创建的流程图列表
     * @return
     */
    @RequestMapping("model")
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
    @RequestMapping(value = "model/create/{name}/{key}", method = RequestMethod.GET)
    @ApiOperation(value = "创建空的流程图，并跳转倒编辑页面", notes = "空流程图无法直接部署")
    @ApiImplicitParams({
            @ApiImplicitParam(name="name", value="流程图名")
            ,@ApiImplicitParam(name="key", value="流程图的key")
    })
    public void create(HttpServletResponse response, @PathVariable String name, @PathVariable String key) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.putPOJO("stencilset", stencilSetNode);
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
    @RequestMapping(value = "model/delete")
    public RestResult delete(String modelId){
        repositoryService.deleteModel(modelId);
        return new RestResult().success("删除流程图成功");
    }

    /**
     * 部署一个流程图
     * @param modelId
     * @return 部署后的流程定义
     */
    @RequestMapping(value = "model/deploy")
    public RestResult deploy(String modelId, String modelKey) throws IOException {
        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
        CustomBpmnJsonConverter bpmnJsonConverter = new CustomBpmnJsonConverter();
        //CustomUserTaskJsonConverter.fillTypes(CustomBpmnJsonConverter.convertersToBpmnMap, CustomBpmnJsonConverter.convertersToJsonMap);
        BpmnModel bpmnModel = bpmnJsonConverter.convertToBpmnModel(modelNode);
        Deployment deployment = repositoryService.createDeployment()
                .addBpmnModel(modelKey + ".bpmn20.xml", bpmnModel)
                .deploy();
        return new RestResult().success().setData(deployment.getId());
    }
}