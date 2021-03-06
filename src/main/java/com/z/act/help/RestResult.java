package com.z.act.help;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "通用的返回格式")
public class RestResult implements Serializable {
    @ApiModelProperty("是否成功")
    private boolean success = true;
    @ApiModelProperty("提示信息")
    private String msg;
    @ApiModelProperty("返回的数据")
    private Object data;

    public RestResult success(){
        this.success = true;
        return this;
    }
    public RestResult fail(){
        this.success = false;
        return this;
    }
    public RestResult success(String msg){
        this.success = true;
        this.msg = msg;
        return this;
    }
    public RestResult fail(String msg){
        this.success = false;
        this.msg = msg;
        return this;
    }
    public RestResult setData(Object data){
        this.data = data;
        return this;
    }
}
