package com.jobengine.job_engine.service;


import com.jobengine.job_engine.model.Job;
import com.jobengine.job_engine.model.JobStatus;
import com.jobengine.job_engine.repository.JobRepository;
import com.jobengine.job_engine.util.BackoffCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetryService {

    private final JobRepository jobRepository;

    @Transactional
    public void handleFailure(Job job , Exception exception){

        int currentAttempt=job.getAttempts()+1;
        job.setAttempts(currentAttempt);

        job.setUpdatedAt(Instant.now());
        job.setLeaseExpiresAt(null);

        job.setErrorMessage(exception.getMessage());

        if(currentAttempt < job.getMaxAttempts()){

            Instant nextRetryTime= BackoffCalculator.nextRetryAt(currentAttempt);
            job.setStatus(JobStatus.RETRYING);
            job.setNextRetryAt(nextRetryTime);


            log.warn("Job failed(attempt {}/{}) - scheduled retry at {}",
                    job.getId(),currentAttempt,job.getMaxAttempts(),nextRetryTime);

        } else{

            job.setStatus(JobStatus.DEAD_LETTER);
            job.setNextRetryAt(null);


            job.setResultPayload(buildErrorPayLoad(exception, currentAttempt));


            log.error("Job {} exhausted all {} attempts - moved to DEAD_LETTER queue",
                    job.getId(),job.getMaxAttempts());

            jobRepository.save(job);
        }
        jobRepository.save(job);

    }

    private String buildErrorPayLoad(Exception e,int totalAttempts){
        StringWriter stringWriter=new StringWriter();
        PrintWriter printWriter=new PrintWriter(stringWriter);

        e.printStackTrace(printWriter);

        return String.format("""
                { 
                  "finalError": "%s",
                  "totalAttempts": %d,
                  "stackTrace": "%s",
                  "movedToDeadLetterAt": "%s"
                  }
                """,
                e.getMessage()!=null ? e.getMessage().replace("\"","'") : "Unknown Error",
                totalAttempts,
                stringWriter.toString().replace("\"","'").replace("\n","\\n").replace("\r",""),
                Instant.now()
        );

    }
}
