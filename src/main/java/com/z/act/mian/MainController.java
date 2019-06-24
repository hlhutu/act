package com.z.act.mian;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

//@RestController
//@RequestMapping("/")
public class MainController {
    /**
     * 视图，流程图设计页面
     * 需要一个参数，modelId：设计的流程图id
     * @return
     */
    @RequestMapping("modeler")
    public ModelAndView modeler(){
        ModelAndView mav = new ModelAndView("modeler");
        return mav;
    }
}
