package com.z.act.help;

import java.io.Serializable;

public class RestResult implements Serializable {
    private boolean success = true;
    private String msg = null;
    private Object data = null;

    public RestResult(){
    }

    public RestResult(String msg){
        this.msg = msg;
    }

    public RestResult(String msg, boolean success){
        this.msg = msg;
        this.success = success;
    }

    public RestResult setData(Object data){
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }
}
