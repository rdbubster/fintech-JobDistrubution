package com.jobengine.job_engine.repository;

import com.jobengine.job_engine.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

  Optional<Job> findByIdempotencyKey(String idempotencyKey);

  //@Modifying(clearAutomatically = true,flushAutomatically = true)

@Query(value= """
        UPDATE jobs SET
            status='RUNNING',
            lease_expires_at=:leaseExpiry,
            updated_at=NOW()
        WHERE id=(
            SELECT id FROM jobs
            WHERE (
                status='PENDING'
                OR (status='RETRYING' AND next_retry_at <= NOW())
            )
            ORDER BY
                 CASE priority
                      WHEN 'HIGH'    THEN 1
                      WHEN 'MEDIUM'  THEN 2
                      WHEN 'LOW'     THEN 3 
                 END,
                 created_at ASC
            FOR UPDATE SKIP LOCKED
            LIMIT 1
        )
        RETURNING *
        """, nativeQuery = true)
  Optional<Job> claimNextJob(@Param("leaseExpiry") Instant leaseExpiry);

  @Query(value = "SELECT * FROM jobs WHERE status ='RUNNING' AND lease_expires_at < :now",nativeQuery = true)
  List<Job> findExpiredLeases(@Param("now") Instant now);

}
