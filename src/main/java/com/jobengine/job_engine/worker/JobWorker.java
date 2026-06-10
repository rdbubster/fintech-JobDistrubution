package com.jobengine.job_engine.worker;

import com.jobengine.job_engine.handler.JobHandler;
import com.jobengine.job_engine.handler.JobHandlerRegistry;
import com.jobengine.job_engine.model.Job;
import com.jobengine.job_engine.model.JobStatus;
import com.jobengine.job_engine.repository.JobRepository;
import com.jobengine.job_engine.service.RetryService; // 1. Import our new specialist bean!
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobWorker {

    private final JobRepository jobRepository;
    private final JobHandlerRegistry handlerRegistry;
    private final RetryService retryService; // 2. Demand the retry specialist via constructor injection

    @Async("jobExecutor")
    @Transactional
    public void execute(Job job) {
        // Line 1: Log the worker entry block along with its current systemic iteration bounds
        log.info("🚀 Worker picked up job {} of type {} (attempt {}/{})",
                job.getId(), job.getType(), job.getAttempts() + 1, job.getMaxAttempts());

        try {
            // Line 2: Route the job object payload to its respective functional strategy desk
            JobHandler handler = handlerRegistry.getHandler(job.getType());
            handler.handle(job);

            // Line 3: If execution finishes without a crash, permanently stamp it complete!
            markCompleted(job);

        } catch (Exception e) {
            // Line 4: Catch ANY structural exception warning light thrown by the handler code
            log.error(" Job {} threw an exception message: {}", job.getId(), e.getMessage());

            // Line 5: Hand the broken pieces over to our specialist retry service to process!
            retryService.handleFailure(job, e);
        }
    }

    private void markCompleted(Job job) {
        job.setStatus(JobStatus.COMPLETED);
        job.setCompletedAt(Instant.now());
        job.setUpdatedAt(Instant.now());
        job.setLeaseExpiresAt(null);
        jobRepository.save(job);
        log.info("Job {} COMPLETED successfully", job.getId());
    }
}