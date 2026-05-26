package com.jobengine.job_engine.model;

public enum JobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    RETRYING,
    DEAD_LETTER,
    CANCELLED
}
