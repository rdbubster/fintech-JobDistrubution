package com.jobengine.job_engine.handler;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JobHandlerRegistry {

private final Map<String,JobHandler> handlers;

public JobHandlerRegistry(List<JobHandler> handlerList){
    this.handlers=handlerList.stream()
            .collect(Collectors.toMap(
                    handler ->handler.getType(),
                    Function.identity()
            ));
}

public JobHandler getHandler(String type){
    JobHandler handler=handlers.get(type);
    if(handler==null){
        throw new IllegalArgumentException("No handler registered for job type: "+type);
    }
    return handler;
}

}
