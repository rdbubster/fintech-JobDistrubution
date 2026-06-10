package com.jobengine.job_engine.handler;

import com.jobengine.job_engine.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenerateStatementHandler implements JobHandler {

    @Override
    public String getType() {
        return "GENERATE_STATEMENT";
    }

    @Override
    public void handle(Job job) throws Exception {

        if (job.getAttempts() < 2) {
            throw new RuntimeException("Simulated External Clearing-House Timeout on Attempt " + (job.getAttempts() + 1));
        }
log.info("Starting statement generation for job {}",job.getId());

int totalRows=10000;
int chunkSize=500;
int totalChunks=totalRows/chunkSize;
for (int i = 0; i < totalChunks; i++) {
            // Simulate processing CPU load iterations
            Thread.sleep(200);

            log.info("Job {} — chunk {}/{} processed", job.getId(), i + 1, totalChunks);
        }

        log.info("Statement generation complete for job {}", job.getId());
    }
}