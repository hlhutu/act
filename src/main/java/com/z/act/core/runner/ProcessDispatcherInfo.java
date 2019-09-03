package com.z.act.core.runner;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ProcessDispatcherInfo implements Serializable {
    private String name;
    private boolean required;
}
