package com.jobengine.job_engine.worker;


import com.jobengine.job_engine.model.Job;
import com.jobengine.job_engine.model.JobStatus;
import com.jobengine.job_engine.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobDispatcher {
    private final JobRepository jobRepository;
    private final JobWorker jobWorker;

@Scheduled(fixedDelay=2000)
    @Transactional
    public void dispatch(){

    Optional<Job> claimed= jobRepository.claimNextJob(Instant.now().plusSeconds(30));

claimed.ifPresent(job-> {
    log.info("Dispatch claimed job {} - dispatching to worker", job.getId());
jobWorker.execute(job);

});

}

@Scheduled(fixedDelay = 10000)
@Transactional
public void reclaimExpiredLeases(){

        List<Job> expiredJobs = jobRepository.findExpiredLeases(Instant.now());

        if(!expiredJobs.isEmpty()){
            log.warn("Found {} expired job leases from crashed worker instances!",expiredJobs.size());
        }

        expiredJobs.forEach(job ->{
            log.warn("Reclaiming expired lease for job {} - resetting state back to PENDING",job.getId());

            job.setStatus(JobStatus.PENDING);

            job.setLeaseExpiresAt(null);
            job.setUpdatedAt(Instant.now());

            jobRepository.save(job);

        });


}
}
