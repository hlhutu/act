package com.z.act.activiti.comm;

public enum TaskAction {
    start, complete, back, reject, jump, cancel, delete, assign;

    public boolean equals(String s){
        return this.name().equals(s);
    }
}
