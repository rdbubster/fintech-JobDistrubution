CREATE TABLE jobs(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL ,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    idempotency_key VARCHAR(255) UNIQUE ,
    attempts INT NOT NULL DEFAULT 0,
    max_attempts INT NOT NULL DEFAULT 3,
    lease_expires_at TIMESTAMP,
    next_retry_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP,
    result_payload JSONB,
    error_message TEXT


);

CREATE INDEX idx_jobs_status_priority_created
ON jobs(status,priority DESC , created_at ASC);

CREATE INDEX idx_jobs_idempotency_key
ON jobs(idempotency_key)
WHERE idempotency_key IS NOT NULL;