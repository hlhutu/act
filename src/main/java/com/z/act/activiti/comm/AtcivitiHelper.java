package com.z.act.activiti.comm;

import org.activiti.bpmn.model.FlowElement;

import java.util.concurrent.atomic.AtomicReference;

public class AtcivitiHelper {
    public static String getDelegateTaskProperty(FlowElement flowElement, final String key1){
        return getDelegateTaskProperty(flowElement, key1, key1);

    }

    public static String getDelegateTaskProperty(FlowElement flowElement, final String key1, final String key2){
        AtomicReference<String> result = new AtomicReference<>();
        flowElement.getExtensionElements().forEach((key, list)->{
            if(key1.equals(key)){
                list.forEach(prop->{
                    if(key2.equals(prop.getName())){
                        result.set(prop.getElementText());
                        return;
                    }
                });
                return;
            }
        });
        return result.get();
    }
}
