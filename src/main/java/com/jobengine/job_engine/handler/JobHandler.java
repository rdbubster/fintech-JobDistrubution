package com.jobengine.job_engine.handler;

import com.jobengine.job_engine.model.Job;

public interface JobHandler {

    String getType() ;

    void handle(Job job) throws Exception ;

}
