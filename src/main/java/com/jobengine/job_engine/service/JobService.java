package com.jobengine.job_engine.service;


import com.jobengine.job_engine.dto.JobSubmitRequest;
import com.jobengine.job_engine.model.Job;
import com.jobengine.job_engine.model.JobStatus;
import com.jobengine.job_engine.model.Priority;
import com.jobengine.job_engine.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;

    @Transactional
    public Job submit(JobSubmitRequest request){

        if(request.getIdempotencyKey() !=null){
            Optional<Job> existing = jobRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if(existing.isPresent()){
                log.info("Idempotent request caught - returning existing job record {}",existing.get().getId());
return existing.get();

            }
        }


        Job job =Job.builder()
                .type(request.getType())
                .payload(request.getPayload())
                .status(JobStatus.PENDING)
                .priority(request.getPriority() != null ? request.getPriority(): Priority.MEDIUM )
                .idempotencyKey(request.getIdempotencyKey())
                .attempts(0)
                .maxAttempts(3)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();


        Job saved=jobRepository.save(job);
        log.info("Successfully ingested job {} of type {} with priority {}",saved.getId(),saved.getType(),saved.getPriority());
        return saved;

   }
   @Transactional(readOnly=true)
    public Job findById(UUID id){
        return jobRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Job record not found: "+ id));
   }

    @Transactional
    public Job retryDeadLetter(UUID id) {
        // Line 1: Fetch the job out of the database table by its unique tracking ID
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + id));

        // Line 2: Enforce a strict state machine boundary check rule
        if (job.getStatus() != JobStatus.DEAD_LETTER) {
            throw new RuntimeException(
                    " Safety Violation: Only DEAD_LETTER jobs can be manually replayed. " +
                            "Current status of this job is: " + job.getStatus());
        }

        // Line 3: Reset the system parameters completely for a clean slate start
        job.setStatus(JobStatus.PENDING);       // Shifting back to the starting line
        job.setAttempts(0);                     // Wiping the failure history counter
        job.setErrorMessage(null);              // Clearing old error warning strings
        job.setResultPayload(null);             // Purging the saved error stack trace
        job.setNextRetryAt(null);               // Removing old scheduling delays
        job.setLeaseExpiresAt(null);            // Stripping any leftover thread locks
        job.setUpdatedAt(Instant.now());        // Recording the timestamp of this reset

        // Line 4: Flush the clean new values back down onto the PostgreSQL disk
        return jobRepository.save(job);
    }

   @Transactional
    public Job cancel(UUID id){

        Job job=findById(id);

        if(job.getStatus() != JobStatus.PENDING){
            throw new IllegalStateException(
                    "Cannot cancel job in status: "+ job.getStatus() +
                            ". Only PENDING jobs can be cancelled safely."
            );
        }

        job.setStatus(JobStatus.CANCELLED);
        job.setUpdatedAt(Instant.now());

        log.info("Job {} successfully cancelled",id);
        return jobRepository.save(job);


   }

}
