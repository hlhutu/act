package com.z.act.core.runner;

import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//@ConfigurationProperties("dispatcher")
@Data
public class DispatcherService implements CommandLineRunner {
    List<ProcessDispatcherInfo> chain;
    int acceptLimit;

    @Order(9999)//数值越大，越后执行
    @Override
    public void run(String... args) {
        //System.out.println("派单chain：" + chain);
        //System.out.println("接单上限：" + acceptLimit);
    }
}
