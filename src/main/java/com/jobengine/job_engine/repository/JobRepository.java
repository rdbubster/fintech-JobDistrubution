package com.jobengine.job_engine.repository;

import com.jobengine.job_engine.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

  Optional<Job> findByIdempotencyKey(String idempotencyKey);



}
