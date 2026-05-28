package com.jobengine.job_engine.handler;

import com.jobengine.job_engine.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendPayoutHandler implements JobHandler{

    @Override
    public String getType() {
        return "SEND_PAYOUT";
    }

    @Override
    public void handle(Job job) throws Exception {

        log.info("Sending Payout for job {}",job.getId());

        Thread.sleep(2000);

        log.info("Payout is sent for the job {}",job.getId());


    }




}
