package com.jobengine.job_engine.handler;


import com.jobengine.job_engine.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenerateStatementHandler implements JobHandler{

    @Override
    public String getType(){

        return "GENERATE_STATEMENT";
    }

    @Override
    public void handle(Job job) throws Exception {
        log.info("Starting statement generation for job {}",job.getId());
        Thread.sleep(2000);
        log.info("Statement generation complete for job {}",job.getId());
    }


}
