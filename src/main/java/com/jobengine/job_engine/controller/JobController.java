package com.jobengine.job_engine.controller;


import com.jobengine.job_engine.dto.JobSubmitRequest;
import com.jobengine.job_engine.model.Job;
import com.jobengine.job_engine.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor

public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<Job> submit(@RequestBody JobSubmitRequest request){
        return ResponseEntity.ok(jobService.submit(request));

    }

    // We use @PostMapping because this action triggers a state change mutation reset sequence
    @PostMapping("/{id}/retry")
    public ResponseEntity<Job> retry(@PathVariable UUID id) {
        // Delegate the ID parameter straight to our service layer reset mechanism!
        return ResponseEntity.ok(jobService.retryDeadLetter(id));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Job> getById(@PathVariable UUID id){
        return ResponseEntity.ok(jobService.findById(id));
    }


    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Job> cancel(@PathVariable UUID id){
        return ResponseEntity.ok(jobService.cancel(id));
      //  return ResponseEntity.ok(cancel(id));


    }


}
