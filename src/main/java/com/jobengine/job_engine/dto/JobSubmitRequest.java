package com.jobengine.job_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jobengine.job_engine.model.Priority;
import lombok.Data;

@Data
public class JobSubmitRequest {

    private String type;

    private String payload;

    private Priority priority;
    @JsonProperty("idempotencyKey")
    private String IdempotencyKey;
}
