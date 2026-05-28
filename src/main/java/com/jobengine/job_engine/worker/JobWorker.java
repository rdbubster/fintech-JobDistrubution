package com.jobengine.job_engine.worker;


import com.jobengine.job_engine.handler.JobHandler;
import com.jobengine.job_engine.handler.JobHandlerRegistry;
import com.jobengine.job_engine.model.Job;
import com.jobengine.job_engine.model.JobStatus;
import com.jobengine.job_engine.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

//import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobWorker {

    private final JobRepository jobRepository;
    private final JobHandlerRegistry handlerRegistry;

    @Async("jobExecutor")
    @Transactional
    public void execute(Job job){
        log.info("Worker picked up job {} of type {}",job.getId(),job.getType());

        try{
            JobHandler handler=handlerRegistry.getHandler(job.getType());

            handler.handle(job);

            markCompleted(job);
        }catch(Exception e){
            log.error("Job {} failed with error: {}",job.getId(),e.getMessage());
            markFailed(job,e.getMessage());
        }
    }
private void markCompleted(Job job){
        job.setStatus(JobStatus.COMPLETED);
        job.setCompletedAt(Instant.now());
        job.setUpdatedAt(Instant.now());
        job.setLeaseExpiresAt(null);
        jobRepository.save(job);
        log.info("Job {} COMPLETED successfully",job.getId());

}

private void markFailed(Job job,String error){
        job.setStatus(JobStatus.FAILED);
        job.setErrorMessage(error);
        job.setUpdatedAt(Instant.now());
        job.setLeaseExpiresAt(null);
        jobRepository.save(job);
        log.warn("Job {} FAILED: {}",job.getId(),error);
}


}
